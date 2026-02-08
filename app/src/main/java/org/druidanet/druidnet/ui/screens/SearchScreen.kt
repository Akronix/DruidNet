package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.mikepenz.markdown.m3.markdownTypography
import org.druidanet.druidnet.R
import org.druidanet.druidnet.component.ShowUsagesButton
import org.druidanet.druidnet.model.PlantCard
import org.druidanet.druidnet.model.PlantUseCard
import org.druidanet.druidnet.navigation.NavigationDestination
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.components.SearchToolbar
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.assetsToBitmap
import kotlin.collections.isNotEmpty

@Composable
fun SearchScreen(
    navigateBack: () -> Unit,
    viewModel: DruidNetViewModel,
    onClickGoToUsage: (PlantUseCard) -> Unit,
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

    val resultPlantList : List<PlantUseCard> by viewModel.searchUses(searchQuery).collectAsState(emptyList())

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
                    onClickPlantUseCard = onClickGoToUsage,
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

fun truncateMarkdown(
    markdown: String,
    maxChars: Int = 80
): String {
    return if (markdown.length <= maxChars) markdown
    else markdown.take(maxChars).trimEnd() + "…"
}


@Composable
private fun ResultPlantCard(
    plantUseCard: PlantUseCard,
    onClickPlantUseCard: (PlantUseCard) -> Unit,
    modifier: Modifier
) {
    val isInPreview = LocalInspectionMode.current
    val imageBitmap =
        if (isInPreview)
            ImageBitmap.imageResource(R.drawable.confused_druidess)
        else
            LocalContext.current.assetsToBitmap(plantUseCard.plant.imagePath)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        onClick = { onClickPlantUseCard(plantUseCard) },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row (
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "{plant.displayName}",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // If we want the card to be taller / more inner padding
            //Spacer(modifier = Modifier.width(6.dp))

            Column (
                modifier = Modifier.weight(1f)
                    .padding(start = 5.dp)
            ) {
                Text(
                    text = plantUseCard.plant.displayName,
                    //modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold,
                    fontStyle = if (plantUseCard.plant.isLatinName) FontStyle.Italic else FontStyle.Normal
                )
                com.mikepenz.markdown.m3.Markdown(
                    content = truncateMarkdown(plantUseCard.text),
                    typography = markdownTypography(
                        paragraph = MaterialTheme.typography.bodyMedium,
                    )
                )

                /*Text(
                    text = plantUseCard.text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    //modifier = Modifier.padding(5.dp)
                )
                 */
            }

        }
    }
}


@Composable
private fun ResultsPlantList(
    plantsList: List<PlantUseCard>,
    onClickPlantUseCard: (PlantUseCard) -> Unit,
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
                key = { it.usageId }
            ) { plantUseCard ->
                ResultPlantCard(
                    plantUseCard = plantUseCard,
                    onClickPlantUseCard = onClickPlantUseCard,
                    modifier = Modifier.padding(0.dp)
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 64.dp)
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

@Preview(showBackground = true)
@Composable
private fun ResultPlantCardPreview() {
    DruidNetTheme(darkTheme = true) {
        ResultPlantCard(
            plantUseCard = PlantUseCard(
                plant = PlantCard(
                    plantId = 1,
                    displayName = "Lavandula angustifolia",
                    imagePath = "plants/lavandula_angustifolia.jpg",
                    latinName = "Lavandula angustifolia",
                    isLatinName = true
                ),
                usageId = 1,
                text = "Medicinal use for anxiety and many other possibilities. Just use it in oil or as a tincture",
                matchOffsets = null
            ),
            onClickPlantUseCard = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}