package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import com.example.model.MatchAnalysisReport
import com.example.model.MatchHistoryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

private const val TAG = "MatchHistoryStore"
private const val HISTORY_DIR_NAME = "match_history"
private const val INDEX_FILE_NAME = "history_index.json"
private const val MAX_HISTORY_ENTRIES = 25
private const val JPEG_QUALITY = 85
private const val THUMBNAIL_MAX_DIM_PX = 360

/**
 * Local file-backed persistence for analyzed MLBB match screenshots and their structured
 * coaching reports. Screenshots are stored as compressed JPEGs under internal app storage
 * (`filesDir/match_history/`), and metadata/reports are stored atomically in JSON.
 */
class MatchHistoryStore(context: Context) {

    private val appContext = context.applicationContext
    private val historyDir: File by lazy {
        File(appContext.filesDir, HISTORY_DIR_NAME).apply {
            if (!exists()) mkdirs()
        }
    }
    private val indexFile: File by lazy { File(historyDir, INDEX_FILE_NAME) }
    private val ioMutex = Mutex()

    // Lightweight in-memory thumbnail cache (~6 MB max) to keep History LazyColumn scrolling smooth
    private val thumbnailCache = object : LruCache<String, Bitmap>(6 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return (value.byteCount / 1024).coerceAtLeast(1)
        }
    }

    suspend fun loadEntries(): List<MatchHistoryEntry> = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            readEntriesLocked()
        }
    }

    suspend fun saveEntry(
        bitmap: Bitmap,
        report: MatchAnalysisReport,
        languageCode: String
    ): MatchHistoryEntry = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            if (!historyDir.exists()) historyDir.mkdirs()

            val entryId = UUID.randomUUID().toString()
            val imageFile = File(historyDir, "$entryId.jpg")

            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }

            val newEntry = MatchHistoryEntry(
                id = entryId,
                analyzedAtMillis = System.currentTimeMillis(),
                screenshotFilePath = imageFile.absolutePath,
                languageCode = languageCode,
                report = report
            )

            val existing = readEntriesLocked()
            val updated = (listOf(newEntry) + existing)
            val kept = updated.take(MAX_HISTORY_ENTRIES)
            val evicted = updated.drop(MAX_HISTORY_ENTRIES)

            evicted.forEach { oldEntry ->
                deleteFileQuietly(oldEntry.screenshotFilePath)
                thumbnailCache.remove(oldEntry.screenshotFilePath)
            }

            writeEntriesLocked(kept)
            newEntry
        }
    }

    suspend fun deleteEntry(entryId: String): List<MatchHistoryEntry> = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val existing = readEntriesLocked()
            val target = existing.firstOrNull { it.id == entryId }
            val updated = existing.filterNot { it.id == entryId }

            if (target != null) {
                deleteFileQuietly(target.screenshotFilePath)
                thumbnailCache.remove(target.screenshotFilePath)
            }

            writeEntriesLocked(updated)
            updated
        }
    }

    suspend fun clearAll(): List<MatchHistoryEntry> = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            thumbnailCache.evictAll()
            historyDir.listFiles()?.forEach { file ->
                try {
                    file.delete()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to delete history file: ${file.name}", e)
                }
            }
            emptyList()
        }
    }

    suspend fun loadThumbnail(filePath: String): Bitmap? = withContext(Dispatchers.IO) {
        if (filePath.isBlank()) return@withContext null
        thumbnailCache.get(filePath)?.let { cached ->
            if (!cached.isRecycled) return@withContext cached
        }
        val decoded = decodeSampledBitmap(filePath, THUMBNAIL_MAX_DIM_PX)
        if (decoded != null) {
            thumbnailCache.put(filePath, decoded)
        }
        decoded
    }

    suspend fun loadFullBitmap(filePath: String, maxDimensionPx: Int = 1280): Bitmap? =
        withContext(Dispatchers.IO) {
            if (filePath.isBlank()) return@withContext null
            decodeSampledBitmap(filePath, maxDimensionPx)
        }

    private fun decodeSampledBitmap(filePath: String, maxDimensionPx: Int): Bitmap? {
        val file = File(filePath)
        if (!file.exists()) return null

        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            val width = bounds.outWidth
            val height = bounds.outHeight
            if (width <= 0 || height <= 0) return null

            var sampleSize = 1
            while (width / sampleSize > maxDimensionPx || height / sampleSize > maxDimensionPx) {
                sampleSize *= 2
            }

            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            BitmapFactory.decodeFile(file.absolutePath, decodeOpts)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode history bitmap at $filePath", e)
            null
        }
    }

    private fun readEntriesLocked(): List<MatchHistoryEntry> {
        if (!indexFile.exists()) return emptyList()
        return try {
            val raw = indexFile.readText(Charsets.UTF_8)
            if (raw.isBlank()) return emptyList()
            val array = JSONArray(raw)
            val result = ArrayList<MatchHistoryEntry>(array.length())
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                parseEntry(obj)?.let { result.add(it) }
            }
            result.sortedByDescending { it.analyzedAtMillis }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read match history index", e)
            emptyList()
        }
    }

    private fun writeEntriesLocked(entries: List<MatchHistoryEntry>) {
        try {
            if (!historyDir.exists()) historyDir.mkdirs()
            val array = JSONArray()
            entries.forEach { entry ->
                array.put(serializeEntry(entry))
            }
            val tmpFile = File(historyDir, "$INDEX_FILE_NAME.tmp")
            tmpFile.writeText(array.toString(), Charsets.UTF_8)
            if (!tmpFile.renameTo(indexFile)) {
                indexFile.writeText(array.toString(), Charsets.UTF_8)
                tmpFile.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to persist match history index", e)
        }
    }

    private fun serializeEntry(entry: MatchHistoryEntry): JSONObject {
        val report = entry.report
        val reportObj = JSONObject().apply {
            put("matchResultFa", report.matchResultFa)
            put("heroPlayed", report.heroPlayed)
            put("kda", report.kda)
            put("gold", report.gold)
            put("medalFa", report.medalFa)
            put("detectedItems", JSONArray(report.detectedItems))
            put("enemyHeroes", JSONArray(report.enemyHeroes))
            put("strengths", JSONArray(report.strengths))
            put("mistakes", JSONArray(report.mistakes))
            put("nextMatchGoal", report.nextMatchGoal)
        }

        return JSONObject().apply {
            put("id", entry.id)
            put("analyzedAtMillis", entry.analyzedAtMillis)
            put("screenshotFilePath", entry.screenshotFilePath)
            put("languageCode", entry.languageCode)
            put("report", reportObj)
        }
    }

    private fun parseEntry(obj: JSONObject): MatchHistoryEntry? {
        val id = obj.optString("id", "").takeIf { it.isNotBlank() } ?: return null
        val analyzedAtMillis = obj.optLong("analyzedAtMillis", 0L)
        val screenshotFilePath = obj.optString("screenshotFilePath", "")
        val languageCode = obj.optString("languageCode", "fa")
        val reportObj = obj.optJSONObject("report") ?: return null

        val report = MatchAnalysisReport(
            matchResultFa = reportObj.optString("matchResultFa", ""),
            heroPlayed = reportObj.optString("heroPlayed", ""),
            kda = reportObj.optString("kda", ""),
            gold = reportObj.optString("gold", ""),
            medalFa = reportObj.optString("medalFa", ""),
            detectedItems = reportObj.optJSONArray("detectedItems").toStringList(),
            enemyHeroes = reportObj.optJSONArray("enemyHeroes").toStringList(),
            strengths = reportObj.optJSONArray("strengths").toStringList(),
            mistakes = reportObj.optJSONArray("mistakes").toStringList(),
            nextMatchGoal = reportObj.optString("nextMatchGoal", "")
        )

        return MatchHistoryEntry(
            id = id,
            analyzedAtMillis = analyzedAtMillis,
            screenshotFilePath = screenshotFilePath,
            languageCode = languageCode,
            report = report
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        val list = ArrayList<String>(length())
        for (i in 0 until length()) {
            val item = optString(i, "").trim()
            if (item.isNotEmpty()) list.add(item)
        }
        return list
    }

    private fun deleteFileQuietly(path: String) {
        if (path.isBlank()) return
        try {
            File(path).delete()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete screenshot file: $path", e)
        }
    }
}
