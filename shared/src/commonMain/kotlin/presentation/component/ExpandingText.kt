
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ExpandingText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onExpandedChange: (Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null)}

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, // Flip arrow 180 degrees when expanded
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
    )

    LaunchedEffect(key1 = textLayoutResultState.value) {
        textLayoutResultState.value?.let { layoutResult ->
            isClickable = layoutResult.hasVisualOverflow || isExpanded
        }
    }

    Row(
        modifier = modifier.clickable(enabled = isClickable) {
            isExpanded = !isExpanded
            onExpandedChange(isExpanded)
        },
        verticalAlignment = if (isExpanded) Alignment.Top else Alignment.CenterVertically // This controls the vertical alignment of the icon
    ) {
        BasicText(
            text = text,
            style = style,
            maxLines = if (isExpanded) Int.MAX_VALUE else MINIMIZED_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResultState.value = it },
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "Expand or collapse",
            modifier = Modifier.graphicsLayer(rotationZ = arrowRotation)
        )
    }
}

private const val MINIMIZED_MAX_LINES = 3
