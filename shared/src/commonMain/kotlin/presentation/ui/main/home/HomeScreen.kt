
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import business.domain.main.ChatMessage
import presentation.ui.main.home.view_model.HomeEvent
import presentation.ui.main.home.view_model.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state = viewModel.state.value
    val scrollState = rememberLazyListState()
    val messages = state.chatMessages.reversed()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatMessagesList(messages = messages, scrollState = scrollState, modifier = Modifier.weight(1f))
            ChatInput(viewModel = viewModel, modifier = Modifier)
        }
    }
}

@Composable
fun ChatMessagesList(messages: List<ChatMessage>, scrollState: LazyListState, modifier: Modifier) {
    LazyColumn(
        state = scrollState,
        modifier = modifier.padding(8.dp),
        reverseLayout = true  // Start from the bottom
    ) {
        items(messages, key = { it.id }) { message ->
            if (message.sender == "User") {
                UserMessageBubble(message)
            } else {
                GptMessageBubble(message)
            }
        }
    }
    // Automatically scroll to the bottom when the list changes
    LaunchedEffect(messages.size) {
        scrollState.animateScrollToItem(index = 0)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(viewModel: HomeViewModel, modifier: Modifier) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .padding(8.dp)
            .background(Color.White)
            .fillMaxWidth()
    ) {
        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20.dp)),
            placeholder = { Text("Type a message") },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        Button(
            onClick = {
                if (text.isNotEmpty()) {
                    viewModel.onTriggerEvent(HomeEvent.OnClickSendMessage(text))
                    text = ""
                }
            },
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}


@Composable
fun UserMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = message.content,
            modifier = Modifier
                .background(Color(0xFF1B6682), RoundedCornerShape(12.dp))
                .padding(8.dp),
            color = Color.White
        )
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun GptMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "GPT",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = message.content,
            modifier = Modifier
                .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(8.dp),
            color = Color(0xFF1B6682)
        )
    }
}
