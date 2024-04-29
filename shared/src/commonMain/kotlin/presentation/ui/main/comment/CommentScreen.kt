package presentation.ui.main.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import business.core.UIComponentState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import presentation.component.AddCommentDialog
import presentation.component.DefaultScreenUI
import presentation.theme.BorderColor
import presentation.ui.main.comment.view_model.CommentEvent
import presentation.ui.main.comment.view_model.CommentState
import presentation.ui.main.detail.CommentBox
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.comments
import shoping_by_kmp.shared.generated.resources.no_comments


@OptIn(ExperimentalResourceApi::class)
@Composable
fun CommentScreen(state: CommentState, events: (CommentEvent) -> Unit, popup: () -> Unit) {

    if (state.addCommentDialogState == UIComponentState.Show) {
        AddCommentDialog(onDismissRequest = {
            events(CommentEvent.OnUpdateAddCommentDialogState(UIComponentState.Hide))
        },
            onExecute = { rate, comment ->
                events(CommentEvent.AddComment(rate = rate, comment = comment))
            })
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DefaultScreenUI(queue = state.errorQueue,
            onRemoveHeadFromQueue = { events(CommentEvent.OnRemoveHeadFromQueue) },
            progressBarState = state.progressBarState,
            networkState = state.networkState,
            onTryAgain = { events(CommentEvent.OnRetryNetwork) },
            titleToolbar = stringResource(Res.string.comments),
            startIconToolbar = Icons.AutoMirrored.Filled.ArrowBack,
            onClickStartIconToolbar = popup,
            endIconToolbar = Icons.Filled.AddComment,
            onClickEndIconToolbar = { events(CommentEvent.OnUpdateAddCommentDialogState(UIComponentState.Show)) }) {
            Column(modifier = Modifier.fillMaxSize()) {


                if (state.comments.isEmpty()) {
                    Text(
                        stringResource(Res.string.no_comments),
                        style = MaterialTheme.typography.titleLarge,
                        color = BorderColor,
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }


                LazyColumn {
                    items(state.comments, key = { it.id }) {
                        CommentBox(comment = it, modifier = Modifier.fillMaxWidth())
                    }
                }

            }
        }
    }
}

