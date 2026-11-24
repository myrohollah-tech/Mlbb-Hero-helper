package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.i18n.LocalAppLanguage
import com.example.i18n.shareStrings
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanContainer

@Composable
fun HeroShareButton(
    onClick: () -> Unit,
    testTag: String,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val shareStrings = remember(language) { language.shareStrings() }

    IconButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .size(48.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    if (isLoading) ElectricCyanContainer.copy(alpha = 0.9f)
                    else MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = ElectricCyan,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = shareStrings.shareHeroCd,
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
