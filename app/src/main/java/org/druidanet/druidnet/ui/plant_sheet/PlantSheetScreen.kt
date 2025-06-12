package org.druidanet.druidnet.ui.plant_sheet

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.DruidNetAppBar
import org.druidanet.druidnet.R
import org.druidanet.druidnet.model.Confusion
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.model.Usage
import org.druidanet.druidnet.utils.assetsToBitmap
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.tooling.preview.Preview
import org.druidanet.druidnet.data.plant.PlantsDataSource
import org.druidanet.druidnet.ui.theme.DruidNetTheme


enum class PlantSheetSection {
    DESCRIPTION, USAGES, CONFUSIONS
}

val DEFAULT_SECTION = PlantSheetSection.DESCRIPTION


@Composable
fun PlantSheetScreen(
    plantLatinName: String,
    navigateBack: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    sheetViewModel: PlantSheetViewModel = viewModel(factory = PlantSheetViewModel.factory),
) {
    val plantSheetUiState = sheetViewModel.uiState.collectAsState().value
    val plant = plantSheetUiState.plantUiState
    val currentSection = plantSheetUiState.currentSection

    val onChangeSection =
        { section: PlantSheetSection -> { sheetViewModel.changeSection(section) } }

    if (plant != null) {
        Scaffold(
            topBar = DruidNetAppBar(
                navigateUp = navigateBack,
                topBarTitle = plant.displayName
            ),
            bottomBar = PlantSheetBottomBar(
                onClickBottomNavItem = onChangeSection,
                currentSection = plantSheetUiState.currentSection,
                hasConfusions = plantSheetUiState.plantHasConfusions
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                )
        ) {padding ->
            PlantSheetBody(
                plant = plant,
                currentSection,
                onChangeSection,
                modifier = modifier.padding(padding)
            )
        }

    } else {
        Scaffold(
            topBar = DruidNetAppBar(
                navigateUp = navigateBack,
                topBarTitle = plantLatinName.replace('_',' '),
                topBarColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                )
        )
        { padding ->
            NoPlantFound(
                plantLatinName,
                modifier = modifier.padding(padding))
        }
    }
}

@Composable
fun NoPlantFound(
    plantLatinName: String,
    modifier : Modifier) {
    Box( modifier ) {
        Column(modifier = Modifier
            .align(Alignment.Center)
            .padding(dimensionResource(R.dimen.padding_large))) {
            Markdown("Todavía no tenemos _${plantLatinName}_ en nuestros registros.",
                modifier = Modifier,
                typography = markdownTypography(
                    paragraph =
                        MaterialTheme.typography.headlineSmall.copy(
                            textAlign = TextAlign.Center)
                )
            )

            Image(painterResource(R.drawable.confused_druidess),
                "Una druidesa confundida observando una planta que desconoce.")
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(20.dp))
                {
                    Markdown("¿Te gustaría contribuir a que _${plantLatinName}_ esté en DruidNet?\n\n\n[Envíanos una lechuza mensajera](mailto:druidnetbeta@gmail.com) \uD83D\uDD4A\uFE0F",
                        modifier = Modifier,
                        typography = markdownTypography(
                            paragraph =
                                MaterialTheme.typography.titleSmall
                                    .copy(textAlign = TextAlign.Center,
                                        fontSize = 18.sp),
                            link = MaterialTheme.typography.titleSmall
                                .copy(textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                ),
                            text = MaterialTheme.typography.titleSmall
                                .copy(textAlign = TextAlign.Center)
                        )
                    )
                }
            }
            Box(Modifier.fillMaxHeight())
        }
    }
}

@Composable
fun PlantSheetBody(
    plant: Plant,
    currentSection: PlantSheetSection,
    onChangeSection: (PlantSheetSection) -> () -> Unit,
    modifier: Modifier = Modifier
) {

    SelectionContainer {

        when (currentSection) {
            PlantSheetSection.DESCRIPTION -> PlantSheetDescription(
                plant,
                onChangeSection(PlantSheetSection.USAGES),
                modifier.verticalScroll(rememberScrollState())
            )

            PlantSheetSection.USAGES -> PlantSheetUsages(
                plant,
                modifier.verticalScroll(rememberScrollState())
            )

            PlantSheetSection.CONFUSIONS -> PlantSheetConfusions(
                plant = plant,
                modifier = modifier.verticalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun PlantSheetDescription(plant: Plant, onClickShowUsages: () -> Unit, modifier: Modifier) {
//    val imgResourceId = LocalContext.current.getResourceId(plant.imagePath)
    val imageBitmap = LocalContext.current.assetsToBitmap(plant.imagePath)

    Column (
        modifier = modifier
            .padding(0.dp)
    ) {
        Box(
            modifier = Modifier
                .height(250.dp)
                .padding(0.dp)
                .zoomable(rememberZoomableState())
        ){
            Image(
                contentScale = ContentScale.FillWidth,
                bitmap = imageBitmap,
                contentDescription = stringResource(R.string.datasheet_image_cdescp),
                modifier = Modifier
                    .fillMaxWidth()

            )
        }
        Column (
            modifier = Modifier.padding(
                top = 20.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 0.dp,
            )

        ) {

            Text(plant.displayName,
                style = MaterialTheme.typography.headlineLarge,
            )

            Text("(${plant.latinName})",
                fontStyle = Italic,
                style = MaterialTheme.typography.titleLarge,
            )
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(10.dp)
            )

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle( fontWeight = FontWeight.Bold )) {
                        append(stringResource(R.string.datasheet_common_names))
                    }
                    append(" ")
                    append(plant.commonNames.joinToString(
                        transform = {name -> "${name.name} (${name.language.abbr})"}
                    ))
                    append(".")
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_sections)
            ))
            Markdown(
                plant.description,
                typography = markdownTypography(text = MaterialTheme.typography.bodyMedium),
            )
            Spacer(modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_sections)
            ))
            Column {
                Text("Distribución y Habitat:",
                    style = MaterialTheme.typography.titleMedium)
                Markdown(plant.habitat,
                    typography = markdownTypography(text = MaterialTheme.typography.bodyMedium),
                )
                Markdown(plant.distribution,
                    typography = markdownTypography(text = MaterialTheme.typography.bodyMedium),
                )
            }
            Spacer(modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_sections)
            ))
            Column {
                Text("Fenología:",
                    style = MaterialTheme.typography.titleMedium)
                Markdown(plant.phenology,
                    typography = markdownTypography(text = MaterialTheme.typography.bodyMedium))
            }

            if (plant.observations != null) {
                Spacer(modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_sections)
                ))
                Column {
                    Text("Observaciones:",
                        style = MaterialTheme.typography.titleMedium)
                    Markdown(plant.observations,
                        typography = markdownTypography(text = MaterialTheme.typography.bodyMedium))
                }
            }

            if (plant.curiosities != null) {
                Spacer(modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_sections)
                ))
                Column {
                    Text("Curiosidades:",
                        style = MaterialTheme.typography.titleMedium)
                    Markdown(plant.curiosities,
                        typography = markdownTypography(text = MaterialTheme.typography.bodyMedium))
                }
            }

            Spacer(modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_sections)
            ))

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onClickShowUsages)
                    .padding(bottom = 10.dp)

            )
            {
                Text(
                    "Ver USOS ",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 18.sp),
                    textDecoration = TextDecoration.Underline,
                )
                Icon(
                    painter = painterResource(R.drawable.indian_arrow),
                    contentDescription = null,
                    modifier = Modifier.height(28.dp)
                )
            }

        }
    }
}

@Composable
fun PlantSheetConfusions(plant: Plant, modifier: Modifier) {
    Column ( modifier =
        modifier.padding(
            top = 20.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 0.dp)
    ) {
        Text("Posibles confusiones...",
            style = MaterialTheme.typography.titleLarge)
        if (plant.confusions.isEmpty() )
            Text("No hay registradas plantas peligrosas con las que confundirse.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 20.dp))
        else {
            plant.confusions.forEach { ConfusionTextBox(it) }
        }
    }
}

@Composable
fun ConfusionTextBox(confusion: Confusion) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Markdown(
                content = confusion.latinName,
                typography = markdownTypography(
                    link = MaterialTheme.typography.titleMedium.copy(
                        fontStyle = Italic,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    ),
                    paragraph = MaterialTheme.typography.titleMedium.copy(
                        fontStyle = Italic,
                        fontWeight = FontWeight.Bold,
                    ),
                ),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Markdown(
                content = confusion.text,
                typography = markdownTypography(text = MaterialTheme.typography.bodyLarge),
            )
            if (confusion.imagePath != null) {
                val imageBitmap = LocalContext.current.assetsToBitmap(confusion.imagePath)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Imagen del ${confusion.latinName}",
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .zoomable(rememberZoomableState())
                    )
                    if (confusion.captionText != null)
                        Markdown(
                            content = confusion.captionText,
                            typography = markdownTypography(paragraph =
                                MaterialTheme.typography.bodyMedium
                                    .copy(textAlign = TextAlign.Justify)
                            )
                        )
                }
            }
        }
    }

}

@Composable
fun PlantSheetUsages(plant: Plant, modifier: Modifier) {
    val usagesTypes = plant.usages.keys

    Column( modifier =
        modifier.padding(
            top = 20.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 0.dp)
    ) {

        if (plant.toxic && plant.toxic_text != null) {
            ToxicTextBox(plant.toxic_text)
        }

        for (type in usagesTypes) {
            Text(stringResource(type.displayText),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            plant.usages[type]?.forEach {
                    usage: Usage ->
                Text("~ " + usage.subType + " ~",
                    style = MaterialTheme.typography.titleSmall
                )
                Markdown(usage.text,
                    colors = markdownColor(linkText = MaterialTheme.colorScheme.primary),
                    typography = markdownTypography(
                        text = MaterialTheme.typography.bodyLarge,
                        link = MaterialTheme.typography. bodyLarge. copy(
                            fontWeight = FontWeight. Bold,
                            textDecoration = TextDecoration. Underline
                        )
                    )
                )
                Spacer(modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_sections)
                ))
            }
            Spacer(modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_sections)
            ))
        }
        Spacer(modifier = Modifier.padding(
            dimensionResource(id = R.dimen.space_between_sections)
        ))
    }
}

@Composable
fun ToxicTextBox(toxicText: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.padding(bottom = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ){
            Row (
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(painter = painterResource(id = R.drawable.toxic_warning),
                    contentDescription = "Toxic warning icon",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.section_buttom_img))
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("¡Atención!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Markdown(
                content = toxicText,
                typography = markdownTypography(paragraph =
                    MaterialTheme.typography.bodyMedium
                        .copy(textAlign = TextAlign.Center)
                )
            )
        }
    }
}

/* For expanding the image of the plant to full screen */
@Composable
fun FullScreenImage(imageBitmap : ImageBitmap) {
    Surface {
        Column(
            modifier = Modifier
                .padding(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .padding(0.dp)
            ) {
                Image(
                    contentScale = ContentScale.None,
                    bitmap = imageBitmap,
                    contentDescription = stringResource(R.string.datasheet_image_cdescp),
//                    modifier = Modifier
//                        .fillMaxWidth()
                )
            }
        }
    }
}


/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
//@Preview(showBackground = true)
//@Composable
//fun PlantSheetScreenPreview() {
//    DruidNetTheme {
//        PlantSheetBody(
//            PlantsDataSource.loadPlants()[0],
//            currentSection = PlantSheetSection.USAGES,
//            onChangeSection = { { } },
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}

/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
//@Preview(showBackground = true)
//@Composable
//fun NoPlantFoundPreview() {
//    DruidNetTheme(darkTheme = false) {
//        NoPlantFound(
//            "Digitalis purpurea",
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
