package org.druidanet.druidnet.ui.identify

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.data.plant.PlantsDataSource
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.network.PlantResult
import org.druidanet.druidnet.network.SpeciesInfo
import org.druidanet.druidnet.ui.plant_sheet.PlantSheetSection
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.assetsToBitmap

@Composable
fun SuccessScreen(
    mostLikelyPlant: Plant?,
    latinName: String,
    mostLikelyScore: Double,
    goToPlantSheet: (Plant, PlantSheetSection) -> Unit,
    similarPlants: List<PlantResult>,
    goToSimilarPlant: (String, Double) -> Unit,
    modifier: Modifier = Modifier,
    imageBitMap: ImageBitmap? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(2f)
        ) {
            if (mostLikelyPlant != null)
                PlantInfoDruidNet(
                    plant = mostLikelyPlant,
                    score = mostLikelyScore,
                    goToPlantSheetSection = { section -> goToPlantSheet(mostLikelyPlant, section) },
                    imageBitmapExt = imageBitMap
                )
            else if (latinName.isNotEmpty())
                NotInDatabaseScreen(
                    name = latinName,
                    score = mostLikelyScore,
                )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 0.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        Column (
            modifier = Modifier
                .weight(1f)
                .padding(
                    top = 0.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 20.dp
                )
        )
        {
            SimilarPlants(
                similarPlants = similarPlants,
                goToSimilarPlant
            )
        }

    }
}

@Composable
fun PlantInfoDruidNet(plant: Plant,
                      score: Double,
                      imageBitmapExt: ImageBitmap?,
                     goToPlantSheetSection: (PlantSheetSection) -> Unit) {
    val imageBitmap = imageBitmapExt ?: LocalContext.current.assetsToBitmap(plant.imagePath)
    val (confidenceBackgroundColor, confidenceContentColor) = when (score) {
        in 0.5..0.7 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        in 0.7..0.85 -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        in 0.85..1.0 -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Column(
        modifier = Modifier
            .padding(0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .height(250.dp)
                .padding(0.dp)
                .zoomable(rememberZoomableState())
        ) {

            Box(
                Modifier
                    .padding(15.dp)
                    .align(Alignment.TopEnd)
                    .zIndex(1f)
            ) {

            Box(
                modifier = Modifier
                    .wrapContentSize()
//                    .requiredSize()
                    .background(confidenceBackgroundColor, CircleShape)
                    .padding(6.dp),
                    contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(score * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = confidenceContentColor
                    )
                    Text(
                        "Confianza",
                        fontWeight = FontWeight.Bold,
                        color = confidenceContentColor
                    )
                }
            }
        }


            Image(
                contentScale = ContentScale.FillWidth,
                bitmap = imageBitmap,
//                painter = painterResource(R.drawable.eco),
                contentDescription = stringResource(R.string.datasheet_image_cdescp),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 20.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 0.dp,
                )

        ) {

            Column(
                modifier = Modifier
                    .clickable { goToPlantSheetSection(PlantSheetSection.DESCRIPTION) },
            ) {
                Text(
                    plant.displayName,
                    style = MaterialTheme.typography.headlineLarge,
                )

                Text(
                    "(${plant.latinName})",
                    fontStyle = Italic,
                    style = MaterialTheme.typography.titleLarge,
                )

//            Text(
//                "Familia ${plant.family}",
//                style = MaterialTheme.typography.bodyMedium,
//            )

                Text(
                    plant.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    "Leer más",
                    modifier = Modifier
                        .align(Alignment.End),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (plant.confusions.isNotEmpty()) {
                // Possible Confusion Section
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { goToPlantSheetSection(PlantSheetSection.CONFUSIONS) }
                    ) {
                    Icon(painterResource(R.drawable.info),
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.error )
                    Spacer(modifier = Modifier.width(8.dp))
                    val confusionTxt = if (plant.confusions.size == 1) "Hay ${plant.confusions.size} posible confusión" else "Hay ${plant.confusions.size} posibles confusiones"
                        Text(confusionTxt,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error)
                }
            }

            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 6.dp, top = 6.dp),
                onClick = { goToPlantSheetSection(PlantSheetSection.USAGES) },
            ) {
                Icon( painterResource(R.drawable.usages),
                    "Ir a usos",
                    modifier = Modifier.width(dimensionResource(R.dimen.section_buttom_img))
                )
                Text(text = "Ver Usos")
            }

        }
    }
}

@Composable
fun SimilarPlants(
    similarPlants: List<PlantResult>,
    goToSimilarPlant: (String, Double) -> Unit
)
{
    // Other Similar Plants Section
    Text(
        text = "Otras plantas similares",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier. padding(top = 6.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (similarPlants.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(similarPlants) { plantResult ->
                SimilarPlantCard(plantResult, goToSimilarPlant)
            }
        }
    } else {
        Text("No se ha identificado ninguna otra planta similar.", style = MaterialTheme.typography.bodyMedium)
    }

}

@Composable
fun IdentifyScreen(
    identifyViewModel: IdentifyViewModel,
    goToPlantSheet: (Plant, PlantSheetSection) -> Unit,
    modifier: Modifier
) {

    val plantResultUIState by identifyViewModel.uiState.collectAsState()

    Log.i("IdentifyScreen", "Recomposing. Plant in database: Plant: ${plantResultUIState.plant?.displayName}")

//    val goToSimilarPlant = { (p: Plant, s: Double) -> updateUIState(p, s, similarPlants)  }

    // TODO: use the customized version of AppTopbar:
//    Scaffold(
//        topBar = { DruidNetAppBar(
//            navigateUp = if (previousBackStack != CameraScreen) navigateBack else goToWelcomeScreen
//      add thumbnail with input image from user
    Box(modifier = modifier) {
        SuccessScreen(
            mostLikelyPlant = plantResultUIState.plant,
            latinName = plantResultUIState.latinName,
            mostLikelyScore = plantResultUIState.score,
            goToPlantSheet = goToPlantSheet,
            similarPlants = plantResultUIState.similarPlants,
            goToSimilarPlant = { name: String, s: Double ->
                identifyViewModel.updatePlantNetResult(name, s)
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun NotInDatabaseScreen(name: String, score: Double) {
    val (confidenceBackgroundColor, confidenceContentColor) = when (score) {
        in 0.5..0.7 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        in 0.7..0.85 -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        in 0.85..1.0 -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name.replaceFirstChar { it.uppercase() }.replace('_', ' '),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "(No está en nuestra base de datos)",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = Italic,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box {
            Image(
                painter = painterResource(R.drawable.confused_druidess),
                contentDescription = "Druidess looking confused at a plant",
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                Modifier
                    .padding(15.dp)
                    .align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(confidenceBackgroundColor, CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${(score * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = confidenceContentColor
                        )
                        Text(
                            "Confianza",
                            fontWeight = FontWeight.Bold,
                            color = confidenceContentColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Markdown(
                    "**${name.replace('_', ' ')}** todavía no está en nuestros registros.",
                    typography = markdownTypography(
                        paragraph = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Markdown(
                    "¿Te gustaría contribuir a que lo esté?\n\n[¡Envíanos una lechuza mensajera!](mailto:druidnetbeta@gmail.com) 🦉",
                    typography = markdownTypography(
                        link = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer),
                        paragraph = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                    )
                )
            }
        }
    }
}

@Composable
fun SimilarPlantCard(
    plantResult: PlantResult,
    onClickSimilarPlantCard: (String, Double) -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
        onClick = { onClickSimilarPlantCard(plantResult.species?.scientificNameWithoutAuthor ?: "", plantResult.score ?: 0.0) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for similar plant image
                Icon(
                    painterResource(R.drawable.eco),
                    contentDescription = "Similar Plant Image",
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = plantResult.species?.scientificNameWithoutAuthor ?: "Plant",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 2
            )
            Text(
                text = "${(plantResult.score?.times(100))?.toInt() ?: 0}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    // 1. Create a dummy 'Plant' object for the most likely result.
    val dummyPlant = PlantsDataSource.loadPlants()[0]

    // 2. Create a dummy list of 'PlantResult' for similar plants.
    val dummySpeciesInfo2 = SpeciesInfo(
        scientificNameWithoutAuthor = "Papaver rhoeas",
        scientificName = "Papaver rhoeas L.",
        commonNames = listOf("Amapola común")
    )
    val dummyPlantResult2 = PlantResult(score = 0.72, species = dummySpeciesInfo2)

    val dummySpeciesInfo3 = SpeciesInfo(
        scientificNameWithoutAuthor = "Eschscholzia caespitosa",
        scientificName = "Eschscholzia caespitosa Benth.",
        commonNames = listOf("Amapola de mechón")
    )
    val dummyPlantResult3 = PlantResult(score = 0.65, species = dummySpeciesInfo3)

    val similarPlantsList = listOf(dummyPlantResult2, dummyPlantResult3)

    // 3. Render the Composable inside the app's theme.
    DruidNetTheme(darkTheme = true) {
        SuccessScreen(
            mostLikelyPlant = dummyPlant,
            mostLikelyScore = 0.85,
            goToPlantSheet = { _, _ -> { } }, // Dummy lambda for preview
            similarPlants = similarPlantsList,
            goToSimilarPlant = { _,_ -> {} }, // Dummy lambda for preview
            modifier = Modifier.fillMaxSize(),
            latinName = dummyPlant.latinName,
            imageBitMap = ImageBitmap(1, 1)
        )
    }
}
