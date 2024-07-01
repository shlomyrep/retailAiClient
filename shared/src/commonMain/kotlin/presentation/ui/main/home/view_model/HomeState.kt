package presentation.ui.main.home.view_model

import business.core.NetworkState
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.domain.main.ChatMessage
import business.domain.main.Home
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Presentation Module: HomeState.kt
data class HomeState(
    val home: Home = Home(),
    val time: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val chatMessages: List<ChatMessage> = emptyList(),
    val gptMessage: String = "",
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf()),
)

