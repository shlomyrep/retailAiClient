package presentation.component

//import business.domain.main.Product
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import business.datasource.network.main.responses.ProductSelectable
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.theme.BackgroundContent
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.default_image_loader


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ProductBox(
    modifier: Modifier = Modifier.width(180.dp),
    product: ProductSelectable,
    onLikeClick: () -> Unit,
    onClick: () -> Unit
) {
    Box(modifier = modifier.height(260.dp).padding(8.dp).noRippleClickable { onClick() }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(.8f)
                    .clip(MaterialTheme.shapes.small)
            ) {
                AsyncImage(
                    product.image,
                    null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.default_image_loader),
                    placeholder = painterResource(Res.drawable.default_image_loader)
                )
                Box(
                    modifier = Modifier.padding(8.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .noRippleClickable { onLikeClick() }
                            .background(BackgroundContent, CircleShape)
                            .size(30.dp)
                            .padding(6.dp)
                    ) {
                        Icon(
                            if (product.isLike) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxSize()
                        )

                    }
                }
            }
            Spacer_4dp()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                Text(
//                    product.title,
//                    style = MaterialTheme.typography.bodyMedium,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(Icons.Filled.Star, null, tint = orange_400)
//                    Text(product.rate.toString(), style = MaterialTheme.typography.bodySmall)
//                }
            }
            Spacer_4dp()
//            Text(
//                product.getPrice(),
//                style = MaterialTheme.typography.bodyMedium,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
        }
    }
}