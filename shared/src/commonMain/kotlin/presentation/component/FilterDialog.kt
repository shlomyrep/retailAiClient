package presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import business.core.UIComponentState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import presentation.ui.main.search.view_model.SearchEvent
import presentation.ui.main.search.view_model.SearchState
import retailai.shared.generated.resources.Res
import retailai.shared.generated.resources.category
import retailai.shared.generated.resources.filter
import retailai.shared.generated.resources.reset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun FilterDialog(
    state: SearchState,
    events: (SearchEvent) -> Unit,
) {


    var selectedRange by remember {
        mutableStateOf(state.selectedRange)
    }

    val selectedCategories = state.selectedCategory.toMutableStateList()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        BasicAlertDialog(
            onDismissRequest = {
                events(SearchEvent.OnUpdateFilterDialogState(UIComponentState.Hide))
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp)) 
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {

                Spacer_16dp()

                Text(
                    stringResource(Res.string.filter),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer_32dp()

//                Text(
//                    stringResource(Res.string.category) + ":",
//                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
//                    style = MaterialTheme.typography.titleMedium
//                )
//
//                RangeSlider(
//                    value = selectedRange,
//                    onValueChange = { selectedRange = it },
//                    valueRange = state.searchFilter.minPrice
//                        .toFloat()..state.searchFilter.maxPrice.toFloat(),
//                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
//                )
//                Row(
//                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        "$${selectedRange.start.toInt()}",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                    Text(
//                        "$${selectedRange.endInclusive.toInt()}",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//
//                Spacer_16dp()

                Text(
                    stringResource(Res.string.category) + ":",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer_8dp()

                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(state.searchFilter.categories, { it.id }) {
                        CategoryChipsBox(it, isSelected = selectedCategories.contains(it)) {
                            if (selectedCategories.contains(it)) {
                                selectedCategories.remove(it)
                            } else {
                                selectedCategories.add(it)
                            }
                        }
                    }
                }

                Spacer_32dp()

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    DefaultButton(modifier = Modifier.weight(1f), text = stringResource(Res.string.reset)) {
                        events(SearchEvent.OnUpdateSelectedCategory(listOf()))
                        events(SearchEvent.OnUpdatePriceRange(0f..10f))
                        events(SearchEvent.OnUpdateFilterDialogState(UIComponentState.Hide))
                        events(SearchEvent.Search())
                    }
                    Spacer_16dp()
                    DefaultButton(modifier = Modifier.weight(1f), text = stringResource(Res.string.filter)) {
                        events(SearchEvent.OnUpdateSelectedCategory(selectedCategories))
                        events(SearchEvent.OnUpdatePriceRange(selectedRange))
                        events(SearchEvent.OnUpdateFilterDialogState(UIComponentState.Hide))
                        events(
                            SearchEvent.Search(
                                minPrice = selectedRange.start.toInt(),
                                maxPrice = selectedRange.endInclusive.toInt(),
                                categories = selectedCategories
                            )
                        )
                    }
                }

                Spacer_16dp()
            }

        }
    }
}