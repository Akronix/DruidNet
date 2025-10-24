package org.druidanet.druidnet.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.mikepenz.markdown.m3.Markdown
import org.druidanet.druidnet.DruidNetAppBar
import org.druidanet.druidnet.R
import org.druidanet.druidnet.model.PlantCard
import org.druidanet.druidnet.navigation.CatalogDestination
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.assetsToBitmap

@Composable
fun PlantCard(plant: PlantCard, onClickPlantCard: (PlantCard) -> Unit, modifier: Modifier) {

    val imageBitmap = LocalContext.current.assetsToBitmap(plant.imagePath)

    Card(
        onClick = { onClickPlantCard(plant) },
        modifier = modifier
    ) {
        Column {
            Image(
                bitmap = imageBitmap,
                contentDescription = "{plant.displayName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
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
fun PlantsList(
    plantsList: List<PlantCard>,
    onClickPlantCard: (PlantCard) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                plantsList,
                key = { it.plantId }
            ) { plant ->
                PlantCard(
                    plant = plant,
                    onClickPlantCard = onClickPlantCard,
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CatalogScreen(
    onClickPlantCard: (PlantCard) -> Unit,
    listState: LazyListState,
    viewModel: DruidNetViewModel,
    navigateBack: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
//    val searchQuery by viewModel.catalogSearchQuery.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("")}

    val plantList : List<PlantCard> by viewModel.getPlantsFilteredByName(searchQuery).collectAsState(emptyList())

    var isSearchBar by remember { mutableStateOf(false)}

    val catalogTopBar =
        @Composable {
            Crossfade(
                modifier = Modifier.animateContentSize(),
                targetState = isSearchBar,
                label = "Search"
            ) { targetSearchState ->
                if (!targetSearchState) {
                    DruidNetAppBar(
                        topBarTitle = stringResource(CatalogDestination.title),
                        navigateUp = navigateBack,
                        topBarIconPath = CatalogDestination.topBarIconPath,
                        actionIconRes = R.drawable.search_40,
                        actionIconContentDescription = stringResource(R.string.appbar_search_button),
                        onActionClick = { isSearchBar = true }
                    )
                } else {
                    SearchToolbar(
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { searchQuery = it },
                        onSearchTriggered = { searchQuery = it.trim() },
                        onBackClick = { isSearchBar = false; searchQuery = "" },
                        modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    )
                } }
        }

    Scaffold(
        topBar = catalogTopBar,
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            )

    ) {paddingValues ->
        if (plantList.isNotEmpty()) {
            PlantsList(
                plantsList = plantList,
                onClickPlantCard = onClickPlantCard,
                listState = listState,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            NoPlantsScreen(modifier = modifier.padding(paddingValues))
        }
    }
}

@Composable
fun NoPlantsScreen(modifier: Modifier) {
    Box( modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        contentAlignment = Alignment.Center ) {
        Column(modifier = Modifier
            .align(Alignment.Center)
            .padding(dimensionResource(R.dimen.padding_large))) {
            Markdown(
                "Aún no tenemos ninguna planta que coincida con tu búsqueda.\n\n" +
                        "¿Echas algo de menos? [Envíanos un mensaje](mailto:druidnetbeta@gmail.com)"
            )
        }
    }
}

@Composable
private fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        IconButton(onClick = { onBackClick() }) {
            Icon(
                painterResource(R.drawable.arrow_back),
                contentDescription = stringResource(
                    id = R.string.back_button,
                ),
            )
        }
        SearchTextField(
            searchQuery = searchQuery,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
        )
    }
}

@Composable
private fun SearchTextField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearchTriggered(searchQuery)
    }

    TextField(
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        placeholder = {Text(stringResource(R.string.feature_search_textfield))},
        leadingIcon = {
            Icon(
                painterResource(R.drawable.search),
                contentDescription = stringResource(
                    id = R.string.feature_search_title,
                ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChanged("")
                    },
                ) {
                    Icon(
                        painterResource(R.drawable.close),
                        contentDescription = stringResource(
                            id = R.string.feature_search_clear_search_text_content_desc,
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp)
            .padding(bottom = 6.dp, top = 6.dp, start = 10.dp, end = 16.dp)
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    if (searchQuery.isBlank()) return@onKeyEvent false
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag("searchTextField"),
        shape = RoundedCornerShape(32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
//                if (searchQuery.isBlank()) return@KeyboardActions
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun SearchToolbarPreview() {
    DruidNetTheme {
        SearchToolbar(
            searchQuery = "",
            onBackClick = {},
            onSearchQueryChanged = {},
            onSearchTriggered = {},
        )
    }
}

@Preview(showBackground = false, name = "No Plants Found - Dark Theme")
@Composable
fun NoPlantsScreenPreviewDark() {
    DruidNetTheme(darkTheme = true) { // Apply your theme in dark mode
        NoPlantsScreen(Modifier)
    }
}

/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
//@Preview(showBackground = true)
//@Composable
//fun CatalogPreview() {
//    DruidNetTheme {
//        CatalogScreen(
//            plantList = PlantsDataSource.loadPlants()
//                .map { PlantCard(it.plantId, it.displayName, it.imagePath, it.latinName, false) },
//            onClickPlantCard = { },
//            listState = LazyListState(0, 0)
//        )
//    }
//}

/**
 * Composable that displays what the UI of the app looks like in dark theme in the design tab.
 */
//@Preview
//@Composable
//fun CatalogDarkThemePreview() {
//    DruidNetTheme(darkTheme = true) {
//        CatalogScreen(plantList = PlantsDataSource.loadPlants(), onClickPlantCard = { })
//    }
//}
