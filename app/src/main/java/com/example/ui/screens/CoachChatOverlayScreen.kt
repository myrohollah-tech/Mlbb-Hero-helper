package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CoachPersona
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.ui.components.LanguageSelectorButton
import com.example.ui.theme.BorderCyan
import com.example.ui.theme.BorderGold
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.DeepNavy700
import com.example.ui.theme.DeepNavy800
import com.example.ui.theme.DeepNavy900
import com.example.ui.theme.DeepNavy950
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanContainer
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CoachChatViewModel

@Composable
fun CoachChatOverlayScreen(
    viewModel: CoachChatViewModel,
    onClose: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    BackHandler(onBack = onClose)

    LaunchedEffect(language) {
        viewModel.syncLanguage(language)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    val starters = listOf(
        strings.chatbotStarter1,
        strings.chatbotStarter2,
        strings.chatbotStarter3
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy950)
    ) {
        // Header Bar
        Surface(color = DeepNavy900) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("chatbot_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = strings.cdCloseDialog,
                                tint = RoyalGold
                            )
                        }
                        Column {
                            Text(
                                text = strings.chatbotTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = strings.chatbotSubtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LanguageSelectorButton(
                            currentLanguage = language,
                            onClick = onOpenLanguagePicker
                        )
                        IconButton(
                            onClick = { viewModel.resetConversation(language) },
                            modifier = Modifier.testTag("chatbot_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = strings.chatbotResetCd,
                                tint = ElectricCyan
                            )
                        }
                    }
                }

                // Persona Selector Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CoachPersona.entries) { persona ->
                        val selected = uiState.selectedPersona == persona
                        val personaLabel = when (persona) {
                            CoachPersona.GENERAL_COACH -> strings.chatbotPersonaCoach
                            CoachPersona.MACRO_STRATEGIST -> strings.chatbotPersonaMacro
                            CoachPersona.FAST_META_TIPS -> strings.chatbotPersonaFast
                        }
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.selectPersona(persona, language) },
                            label = {
                                Text(
                                    text = personaLabel,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            leadingIcon = if (selected) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DeepNavy800,
                                labelColor = TextSecondary,
                                selectedContainerColor = RoyalGold.copy(alpha = 0.2f),
                                selectedLabelColor = RoyalGold,
                                selectedLeadingIconColor = RoyalGold
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = BorderSubtle,
                                selectedBorderColor = RoyalGold
                            ),
                            modifier = Modifier.testTag("chatbot_persona_${persona.name.lowercase()}")
                        )
                    }
                }
                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
            }
        }

        // Scrollable Multi-Turn Conversation Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.messages.size <= 1) {
                item(key = "starter_prompts") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(starters) { prompt ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = DeepNavy800,
                                border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable(enabled = !uiState.isResponding) {
                                        viewModel.sendMessage(prompt, language)
                                    }
                                    .testTag("chatbot_starter_${starters.indexOf(prompt)}")
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RoyalGold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            items(uiState.messages, key = { it.id }) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (msg.isUser) 18.dp else 4.dp,
                            bottomEnd = if (msg.isUser) 4.dp else 18.dp
                        ),
                        color = if (msg.isUser) ElectricCyanContainer else DeepNavy800,
                        border = BorderStroke(
                            1.dp,
                            if (msg.isUser) BorderCyan else BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (msg.isUser) strings.chatUserLabel else strings.chatCoachLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (msg.isUser) ElectricCyan else RoyalGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.text.ifBlank { strings.chatTypingPlaceholder },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (!uiState.errorMessage.isNullOrBlank()) {
                item(key = "chat_error") {
                    Text(
                        text = uiState.errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = CrimsonAccent
                    )
                }
            }
        }

        // Message Composer Bar
        Surface(color = DeepNavy900) {
            Column {
                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = strings.chatbotInputHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        },
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = DeepNavy800,
                            unfocusedContainerColor = DeepNavy800
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chatbot_input")
                    )
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText, language)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && !uiState.isResponding,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = RoyalGold,
                            contentColor = DeepNavy950,
                            disabledContainerColor = DeepNavy700,
                            disabledContentColor = TextMuted
                        ),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("chatbot_send_button")
                    ) {
                        if (uiState.isResponding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = DeepNavy950,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = strings.chatSendCd
                            )
                        }
                    }
                }
            }
        }
    }
}
