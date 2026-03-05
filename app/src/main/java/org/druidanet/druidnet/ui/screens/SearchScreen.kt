package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import org.druidanet.druidnet.R
import org.druidanet.druidnet.model.PlantCard
import org.druidanet.druidnet.model.PlantUseCard
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.components.SearchToolbar
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.assetsToBitmap

@Composable
fun SearchScreen(
    navigateBack: () -> Unit,
    viewModel: DruidNetViewModel,
    onClickPlantUseCard: (PlantUseCard) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val searchQuery by viewModel.searchUsesQuery.collectAsStateWithLifecycle()

    val searchTopBar =
        @Composable {
            SearchToolbar(
                searchQuery = searchQuery,
                placeholderSearchText = stringResource(R.string.search_uses_textfield),
                onSearchQueryChanged = { viewModel.updateSearchUsesQuery(it) },
                onSearchTriggered = { viewModel.updateSearchUsesQuery(it.trim()) },
                onBackClick = navigateBack,
                modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)
            )
        }

    val resultPlantList : List<PlantUseCard> by viewModel.resultSearchUses.collectAsState(emptyList())

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
                    onClickPlantUseCard = { plantUseCard ->
                        keyboardController?.hide()
                        onClickPlantUseCard(plantUseCard)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                SearchStatusScreen(
                    searchQuery = searchQuery,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

@Composable
private fun SearchStatusScreen(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val isSearchStarted =  searchQuery.isEmpty() || searchQuery.length < 3;
        val noResults = !isSearchStarted
        val (message, iconRes) = when {
            isSearchStarted ->
                stringResource(R.string.search_uses_hint_min_chars) to R.drawable.mystery_search
            else ->
                stringResource(R.string.search_uses_no_results) to R.drawable.confused_druidess
        }

        if (message.isNotEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = if (noResults) Modifier.size(256.dp) else Modifier.size(128.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.70f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private val CLEAN_LINKS_REGEX = Regex("""\(druidnet://[^)]*\)""")

private fun cleanFormatting(text: String): String {
    val noLinksText = CLEAN_LINKS_REGEX.replace(text, "")
    return noLinksText.filter { it != '*' && it != '[' && it != ']' }
}

private fun formatSearchPreview(
    text: String,
    offsetBytes: Int,
    matchSize: Int
): String {
    val maxChars: Int = 80 // max length of the format
    val context = 30 // context

    // 1. Convert string to UTF-8 bytes to match SQLite FTS5 byte offsets
    val fullBytes = text.toByteArray(Charsets.UTF_8)

    // Safety check to avoid crashes if offsets are wrong
    if (
        offsetBytes < 0 ||
        offsetBytes >= fullBytes.size ||
        offsetBytes + matchSize > fullBytes.size
    ) {
        return text.take(maxChars).trimEnd() + "…"
    }

    // 1. Extract part to be highlighted / shaded
    // Use the String(bytes, offset, length, charset) constructor
    var previousText = String(fullBytes, 0, offsetBytes, Charsets.UTF_8)
    val matchText = String(fullBytes, offsetBytes, matchSize, Charsets.UTF_8)
    var restOfText = String(fullBytes,offsetBytes+matchSize, fullBytes.size-offsetBytes-matchSize, Charsets.UTF_8)

    // MicroOptimization:
    // We squeeze the text so we don't do clean operations in too long text
    previousText = previousText.takeLast(context * 3)
    restOfText = restOfText.take(context * 3)

    // 2. clean prefix and suffix of formatting
    previousText = cleanFormatting(previousText)
    restOfText = cleanFormatting(restOfText)

    // 3. Show previous context (previousText) and take the rest until fill maxChars
//    suffixText = suffixText.take(context)
    val previousContext = previousText.takeLast(context)
    val leadingEllipsis = if (previousContext.length == context) "…" else ""
    // 4. Build highlighted text with inline code (``) for the matching text query
    val result = buildString {
        append(leadingEllipsis)
        append(previousContext)
        append('`')
        append(matchText)
        append('`')
        append(restOfText)
    }

    // Add ellipsis when we have cut/omitted some text
    // We show leading … if we had to strip the text from the beginning
//    val leadingEllipsis = if (prefixText.length == context) "…" else ""
    // We show trailing … if there's still text left in the usage text
//    val trailingEllipsis = if (suffixText.length == context) "…" else ""

    /*
    var result = leadingEllipsis + highlighted + trailingEllipsis
    */

    return if (result.length <= maxChars) {
        result // if it fits in maxChars, do not show ellipsis
    } else {
        // Take up to maxChars and add trailing ellipsis
        result.take(maxChars).trimEnd() + "…"
    }
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

    val matchOffsets = plantUseCard.matchOffsets.split(" ")
    val offsetBytes = matchOffsets[2].toInt()
    val matchSize = matchOffsets[3].toInt()

    println(matchOffsets)

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
                modifier = Modifier
                    .weight(1f)
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
                    formatSearchPreview(plantUseCard.text, offsetBytes, matchSize),
                    typography = markdownTypography(
                        paragraph = MaterialTheme.typography.bodyMedium,
                        inlineCode = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            background = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.SemiBold,
                        )
                    ),
                    colors = markdownColor(
                        codeBackground = MaterialTheme.colorScheme.background
                    ),
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
                text = "**Medicinal** **use** for anxiety and many other possibilities. Just use it in oil or as a tincture",
                matchOffsets = "2 0 16 3"
            ),
            onClickPlantUseCard = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}