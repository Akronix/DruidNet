package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import org.druidanet.druidnet.R
import org.druidanet.druidnet.component.ShowUsagesButton
import org.druidanet.druidnet.model.PlantCard
import org.druidanet.druidnet.navigation.NavigationDestination
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.components.SearchToolbar
import org.druidanet.druidnet.utils.assetsToBitmap
import kotlin.collections.isNotEmpty

@Composable
fun SearchScreen(
    navigateBack: () -> Unit,
    viewModel: DruidNetViewModel,
    onClickGoToUsage: (PlantCard) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier
) {

    var searchQuery by remember { mutableStateOf("")}

    val searchTopBar =
        @Composable {
            SearchToolbar(
                searchQuery = searchQuery,
                placeholderSearchText = stringResource(R.string.search_uses_textfield),
                onSearchQueryChanged = { searchQuery = it },
                onSearchTriggered = { searchQuery = it.trim() },
                onBackClick = navigateBack,
                modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)
            )
        }

    val resultPlantList : List<PlantCard> by viewModel.searchUses(searchQuery).collectAsState(emptyList())

    Scaffold(
        topBar = searchTopBar,
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            )

        ) { paddingValues ->

            if (resultPlantList.isNotEmpty()) {
                ResultsPlantList(
                    plantsList = resultPlantList,
                    onClickPlantCard = onClickGoToUsage,
                    onClickShowUsages = onClickGoToUsage,
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                NoResultsScreen(modifier = modifier.padding(paddingValues))
            }
        }
    }

@Composable
private fun NoResultsScreen(modifier: Modifier) {
    Box(modifier) {}
}

@Composable
private fun ResultPlantCard(
    plant: PlantCard,
    onClickPlantCard: (PlantCard) -> Unit,
    onClickShowUsages: (PlantCard) -> Unit,
    modifier: Modifier
) {

    val imageBitmap = LocalContext.current.assetsToBitmap(plant.imagePath)

    Card(
        onClick = { onClickPlantCard(plant) },
        modifier = modifier
    ) {
        Column {
            Box {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "{plant.displayName}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(194.dp),
                    contentScale = ContentScale.Crop
                )
                ShowUsagesButton(
                    onClick = { onClickShowUsages(plant) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
            Text(
                text = plant.displayName,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontStyle = if (plant.isLatinName) FontStyle.Italic else FontStyle.Normal
            )

        }
    }
}


@Composable
private fun ResultsPlantList(
    plantsList: List<PlantCard>,
    onClickPlantCard: (PlantCard) -> Unit,
    onClickShowUsages: (PlantCard) -> Unit,
    modifier: Modifier = Modifier
) {
    var listState by remember { mutableStateOf(LazyListState())}

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                plantsList,
                key = { it.plantId }
            ) { plant ->
                ResultPlantCard(
                    plant = plant,
                    onClickPlantCard = onClickPlantCard,
                    onClickShowUsages = onClickShowUsages,
                    modifier = Modifier.padding(8.dp)
                )

            }

        }

        /* ScrollBar addition: */
        val scrollbarState = listState.scrollbarState(
            itemsAvailable = plantsList.size,
        )
        listState.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = listState.rememberDraggableScroller(
                itemsAvailable = plantsList.size,
            ),
        )
    }
}
