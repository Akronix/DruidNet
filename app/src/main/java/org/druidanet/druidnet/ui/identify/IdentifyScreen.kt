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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.data.plant.PlantsDataSource
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.network.PlantResult
import org.druidanet.druidnet.network.SpeciesInfo
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun SuccessScreen(
    mostLikelyPlant: Plant,
    mostLikelyScore: Double,
    goToPlantSheet: (Plant, String) -> () -> Unit,
    similarPlants: List<PlantResult>,
    goToSimilarPlant: (Plant) -> () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(2f)
        ) {
            MostLikelyPlant(
                plant = mostLikelyPlant,
                score = mostLikelyScore,
                goToPlantSheet = goToPlantSheet(mostLikelyPlant, "DESCRIPTION")
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
                    bottom = 20.dp)
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
fun MostLikelyPlant(plant: Plant,
                    score: Double,
                    goToPlantSheet: () -> Unit) {
//    val imageBitmap = LocalContext.current.assetsToBitmap(plant.imagePath)
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
                Modifier.padding(15.dp)
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
//                bitmap = imageBitmap,
                painter = painterResource(R.drawable.eco),
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
                    .clickable { goToPlantSheet() },
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
                    modifier = Modifier.clickable { goToPlantSheet() }
                    ) {
                    Icon(Icons.Default.Info, contentDescription = "Info",
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
                onClick = { },
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
fun SimilarPlants(similarPlants: List<PlantResult>,
                  goToSimilarPlant: (Plant) -> () -> Unit)
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
                SimilarPlantCard(plantResult)
            }
        }
    } else {
        Text("No se ha identificado ninguna otra planta similar.", style = MaterialTheme.typography.bodyMedium)
    }

}

@Composable
fun IdentifyScreen(
    identifyViewModel: IdentifyViewModel = hiltViewModel(),
    modifier: Modifier) {

    val plantResultUIState by identifyViewModel.uiState.collectAsState()
    val isPlantInDatabase = plantResultUIState.plant != null

    Log.i("IdentifyScreen", "Recomposing. Plant in database: $isPlantInDatabase. Plant: ${plantResultUIState.plant?.displayName}")

    if (plantResultUIState.plant != null) {

        Box(modifier = modifier) {
            // 3. Render the Composable inside the app's theme.
            SuccessScreen(
                mostLikelyPlant = plantResultUIState.plant!!,
                mostLikelyScore = plantResultUIState.score,
                goToPlantSheet = { _, _ -> { } }, // Dummy lambda for preview
                similarPlants = plantResultUIState.similarPlants,
                goToSimilarPlant = { _ -> { } }, // Dummy lambda for preview
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Box(modifier = modifier) {
            NotInDatabaseScreen(
                name = plantResultUIState.latinName
            )
        }
    }
}

@Composable
fun NotInDatabaseScreen(name: String) {
    Text("Not in database $name")
}

@Composable
fun SimilarPlantCard(plantResult: PlantResult) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
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
                text = plantResult.species?.commonNames?.firstOrNull() ?: plantResult.species?.scientificNameWithoutAuthor ?: "Plant",
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
            goToSimilarPlant = { _ -> { } }, // Dummy lambda for preview
            modifier = Modifier.fillMaxSize()
        )
    }
}
