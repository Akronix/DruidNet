package org.druidanet.druidnet.ui.identify

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.network.PlantNetResponse
import org.druidanet.druidnet.network.PlantResult
import org.druidanet.druidnet.network.SpeciesInfo
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun SuccessScreen(
    mostLikelyPlant: Plant,
    mostLikelyScore: Float,
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
            modifier = Modifier.padding(vertical = 24.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        Column (
            modifier = Modifier.weight(1f)
        ){
            SimilarPlants(
                similarPlants = similarPlants,
                goToSimilarPlant
            )
        }

    }
}

@Composable
fun MostLikelyPlant(plant: Plant,
                    score: Float,
                    goToPlantSheet: () -> Unit) {
//    val imageBitmap = LocalContext.current.assetsToBitmap(plant.imagePath)
    Column(
        modifier = Modifier
            .padding(0.dp)
    ) {
        Box(
            modifier = Modifier
                .height(250.dp)
                .padding(0.dp)
                .zoomable(rememberZoomableState())
        ) {
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
            modifier = Modifier.padding(
                top = 20.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 0.dp,
            )

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
        fontWeight = FontWeight.Bold
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
        Text("No se ha identificado otra planta similar.", style = MaterialTheme.typography.bodyMedium)
    }

}

@Composable
fun IdentifyScreen( modifier: Modifier) {
//    SuccessScreen
}

@Composable
fun ResultsIdentifyScreen(
//    identifyViewModel: IdentifyViewModel = hiltViewModel(), // Assuming it might be used or passed through
    // navController: NavController // Add if navigation actions from this screen are needed
    responseData: PlantNetResponse?,
    modifier: Modifier
) {
//    val responseData by identifyViewModel.apiResponse.collectAsState()

    val mainResult = responseData?.results?.firstOrNull()
    val similarPlants = responseData?.results?.drop(1) ?: emptyList()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Image Carousel Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for image
            Icon(
                painter = painterResource(R.drawable.eco),
                contentDescription = "Plant Image",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Carousel Arrows (simplified)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Navigate previous image */ }) {
                    Icon( Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous Image")
                }
                IconButton(onClick = { /* TODO: Navigate next image */ }) {
                    Icon( Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Next Image")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Plant Name and Confidence Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top // Align top for confidence score circle
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mainResult?.species?.commonNames?.firstOrNull() ?: mainResult?.species?.scientificNameWithoutAuthor ?: "Unknown Plant",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "(${mainResult?.species?.scientificName ?: "N/A"})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(mainResult?.score?.times(100))?.toInt() ?: 0}% Confianza",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { /* TODO: Navigate to details screen */ }) {
            Text("Leer más", style = MaterialTheme.typography.labelLarge)
        }

        // Possible Confusion Section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Posible confusión", style = MaterialTheme.typography.titleMedium)
            // TODO: Add details about confusion if available
        }

        Spacer(modifier = Modifier.height(16.dp))

        // View Uses Button
        Button(
            onClick = { /* TODO: Navigate to uses screen or show dialog */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("VER USOS")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )

        // Other Similar Plants Section
        Text(
            text = "Otras plantas similares",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
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
            Text("No other similar plants found.", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp)) // For overall padding at the bottom
    }
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
    val dummyPlant = Plant(
        plantId = 1,
        latinName = "Eschscholzia californica",
        displayName = "Amapola de California",
        imagePath = "", // This is unused as the Composable uses a painterResource
        commonNames = arrayOf(),
        usages = emptyMap(),
        family = "Papaveraceae",
        toxic = false,
        toxic_text = null,
        description = "Description of the plant.",
        habitat = "Habitat of the plant.",
        phenology = "Phenology of the plant.",
        distribution = "Distribution of the plant.",
        confusions = arrayOf()
    )

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
    DruidNetTheme(darkTheme = false) {
        SuccessScreen(
            mostLikelyPlant = dummyPlant,
            mostLikelyScore = 0.85f,
            goToPlantSheet = { _, _ -> { } }, // Dummy lambda for preview
            similarPlants = similarPlantsList,
            goToSimilarPlant = { _ -> { } }, // Dummy lambda for preview
            modifier = Modifier.fillMaxSize()
        )
    }
}

/*
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ResultsIdentifyScreenPreview() {
    // Create a dummy PlantNetResponse for previewing
    val dummySpecies = SpeciesInfo(
        scientificNameWithoutAuthor = "Rosa gallica",
        scientificName = "Rosa gallica L.",
        commonNames = listOf("Amapola", "Poppy"),
        genus = GenusFamilyInfo(scientificName = "Rosa"),
        family = GenusFamilyInfo(scientificName = "Rosaceae")
    )
    val dummyResult1 = PlantResult(score = 0.85, species = dummySpecies)
    val dummyResult2 = PlantResult(score = 0.72, species = SpeciesInfo(commonNames = listOf("Otra Planta"), scientificName = "Bellis perennis"))
    val dummyResult3 = PlantResult(score = 0.65, species = SpeciesInfo(commonNames = listOf("Hierba X"), scientificName = "Taraxacum officinale"))

    val dummyResponse = PlantNetResponse(
        results = listOf(dummyResult1, dummyResult2, dummyResult3),
        bestMatch = "Rosa gallica L."
    )
    DruidNetTheme(darkTheme = false) {
       // For preview, we directly pass a dummy response if the Composable was designed for it.
       // Since
*/
