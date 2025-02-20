package presentation.ui.main.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import presentation.component.DefaultScreenUI
import presentation.component.Spacer_12dp
import presentation.component.Spacer_16dp
import presentation.component.Spacer_32dp
import presentation.component.Spacer_8dp
import presentation.component.noRippleClickable
import presentation.component.rememberCustomImagePainter
import presentation.ui.main.profile.view_model.ProfileEvent
import presentation.ui.main.profile.view_model.ProfileState
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.arrow_left
import retailai.shared.generated.resources.edit_profile
import retailai.shared.generated.resources.order
import retailai.shared.generated.resources.orders
import retailai.shared.generated.resources.profile2
import retailai.shared.generated.resources.setting2
import retailai.shared.generated.resources.settings

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    events: (ProfileEvent) -> Unit,
    navigateToAddress: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToPaymentMethod: () -> Unit,
    navigateToMyOrders: () -> Unit,
    navigateToMyCoupons: () -> Unit,
    navigateToMyWallet: () -> Unit,
    navigateToSettings: () -> Unit,
) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DefaultScreenUI(
            queue = state.errorQueue,
            onRemoveHeadFromQueue = { events(ProfileEvent.OnRemoveHeadFromQueue) },
            progressBarState = state.progressBarState,
            networkState = state.networkState,
            onTryAgain = { events(ProfileEvent.OnRetryNetwork) }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer_32dp()

                Image(
                    painter = rememberCustomImagePainter(state.profile.profileUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer_16dp()

                Text(state.profile.name, style = MaterialTheme.typography.headlineMedium)

                Spacer_32dp()

                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileItemBox(title = stringResource(Res.string.edit_profile), image = Res.drawable.profile2) {
                        navigateToEditProfile()
                    }
//                    ProfileItemBox(
//                        title = stringResource(Res.string.manage_address),
//                        image = Res.drawable.location2
//                    ) { navigateToAddress() }
//                    ProfileItemBox(title = stringResource(Res.string.payment_methods), image = Res.drawable.payment) {
//                        navigateToPaymentMethod()
//                    }
                    ProfileItemBox(title =stringResource(Res.string.orders) , image = Res.drawable.order) {
                        navigateToMyOrders()
                    }
//                    ProfileItemBox(title = stringResource(Res.string.my_coupons), image = Res.drawable.coupon) {
//                        navigateToMyCoupons()
//                    }
                    /*ProfileItemBox(title = "My Wallet", image = "wallet.xml") {
                    navigateToMyWallet()
                }*/
                    ProfileItemBox(title = stringResource(Res.string.settings), image = Res.drawable.setting2) {
                        navigateToSettings()
                    }
//                    ProfileItemBox(title = stringResource(Res.string.help_center), image = Res.drawable.warning, isLastItem = true) {}
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ProfileItemBox(
    title: String,
    image: DrawableResource,
    isLastItem: Boolean = false,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .noRippleClickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(image),
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp)
                )
                Spacer_8dp()
                Text(title, style = MaterialTheme.typography.bodyLarge)
            }

            Icon(
                painterResource(Res.drawable.arrow_left),
                null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = .7f),
                modifier = Modifier.size(30.dp)
            )
        }
        if (!isLastItem) {
            Spacer_12dp()
            HorizontalDivider()
            Spacer_12dp()
        }
    }

}