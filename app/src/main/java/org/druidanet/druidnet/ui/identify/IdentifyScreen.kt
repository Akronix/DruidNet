package org.druidanet.druidnet.ui.identify

import android.graphics.Bitmap
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.component.ShowUsagesButton
import org.druidanet.druidnet.data.plant.PlantsDataSource
import org.druidanet.druidnet.model.Plant
import org.druidanet.druidnet.network.PlantResult
import org.druidanet.druidnet.ui.plant_sheet.PlantSheetSection
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.assetsToBitmap
import org.druidanet.druidnet.utils.forwardingPainter
import org.druidanet.druidnet.utils.sendEmailAction


@Composable
fun ErrorScreen(errorMsg: String, retry: () -> Unit) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.warning),
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Oh, no!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMsg,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center // Centra el texto si ocupa varias líneas
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = retry) {
            Text("Reintentar")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = {
                // 2. Llama a la función para abrir el cliente de email
                sendEmailAction(context)
            }
        ) {
            Text("Informar de un fallo de la app",
                style= MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary)
        }
    }
}

/*
@Preview
@Composable
fun ErrorScreenPreview() {
  DruidNetTheme {
    ErrorScreen(errorMsg = "Ha ocurrido un error inesperado.", retry = {})
  }
}
 */


@Composable
fun LoadingScreen(imageBitmap: Bitmap?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Image being identified",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        // Scrim to darken the background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Identificando...",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}



@Composable
fun SuccessScreen(
    mostLikelyPlant: Plant?,
    latinName: String,
    mostLikelyScore: Double,
    goToPlantSheet: (Plant, PlantSheetSection) -> Unit,
    similarPlants: List<PlantResult>,
    goToSimilarPlant: (String, Double) -> Unit,
    modifier: Modifier = Modifier,
    imageBitMap: ImageBitmap? = null,
    plantNetImageURL: String? = null
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
                    plantNetImageURL = plantNetImageURL
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

            Box(
                Modifier
                    .padding(15.dp)
                    .align(Alignment.TopStart)
                    .zIndex(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.druidnet_logo),
                    contentDescription = "In database badge",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    tint = Color.Unspecified
                )
            }


            Image(
                contentScale = ContentScale.FillWidth,
                bitmap = imageBitmap,
                contentDescription = stringResource(R.string.datasheet_image_cdescp),
                modifier = Modifier
                    .fillMaxWidth()
            )

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 0.dp,
                )

        ) {

            Column(
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
                        .clickable { goToPlantSheetSection(PlantSheetSection.DESCRIPTION) }
                        .padding(4.dp),
                    style = MaterialTheme.typography.titleSmall,
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

            ShowUsagesButton(
                { goToPlantSheetSection(PlantSheetSection.USAGES) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 6.dp, top = 6.dp),
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlantInfoDruidNetPreview() {
    val plant = PlantsDataSource.loadPlants()[0]
    DruidNetTheme {
        PlantInfoDruidNet(
            plant = plant,
            score = 0.92,
            imageBitmapExt = ImageBitmap(1024, 768),
            goToPlantSheetSection = {}
        )
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
        text = "Otras plantas posibles",
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
        Text("No se ha identificado ninguna otra planta posible.", style = MaterialTheme.typography.bodyMedium)
    }

}

@Composable
fun IdentifyScreen(
    identifyViewModel: IdentifyViewModel,
    goToPlantSheet: (Plant, PlantSheetSection) -> Unit,
    navController: NavController,
    modifier: Modifier
) {
    val loading by identifyViewModel.loading.collectAsState()
    val success by identifyViewModel.successRequest.collectAsState()
    val status by identifyViewModel.identificationStatus.collectAsState()
    val plantResultUIState by identifyViewModel.uiState.collectAsState()

    Log.i("IdentifyScreen", "Recomposing. Plant in database: Plant: ${plantResultUIState.plant?.displayName}")

    Box(modifier = modifier) {
        if (loading) {
            LoadingScreen(imageBitmap = null)
        } else {
            if (success) {
                // TODO: use the customized version of AppTopbar to go back to WelcomeScreen?? and include thumbnail :
//    Scaffold(
//        topBar = { DruidNetAppBar(
//            navigateUp = if (previousBackStack != CameraScreen) navigateBack else goToWelcomeScreen
//      add thumbnail with input image from user
                SuccessScreen(
                    mostLikelyPlant = plantResultUIState.plant,
                    latinName = plantResultUIState.latinName,
                    mostLikelyScore = plantResultUIState.score,
                    goToPlantSheet = goToPlantSheet,
                    similarPlants = plantResultUIState.similarPlants,
                    goToSimilarPlant = { name: String, s: Double ->
                        identifyViewModel.updatePlantNetResult(name, s)
                    },
                    plantNetImageURL = plantResultUIState.currentPlantResult?.images?.first()?.url?.o,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                ErrorScreen(status, { navController.navigateUp() } )
            }
        }
    }
}

@Composable
fun NotInDatabaseScreen(name: String, score: Double, plantNetImageURL: String?) {
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

                    Box(
                        Modifier
                            .padding(15.dp)
                            .align(Alignment.TopStart)
                            .zIndex(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.druidnet_logo),
                            contentDescription = "In database badge",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            tint = Color.Unspecified
                        )

                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Not in database cross",
                            modifier = Modifier.size(40.dp), // Make it slightly smaller than the background
                            tint = Color.Red.copy(alpha = 0.8f) // A semi-transparent red is a good choice
                        )
                    }


                    AsyncImage(
                        model = plantNetImageURL,
                        contentDescription = "PlantNet image for $name",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        fallback = painterResource(R.drawable.grass),
                        placeholder = forwardingPainter(
                            painter = painterResource(R.drawable.eco),
                            colorFilter = ColorFilter.tint(Color.Gray),
                            alpha = 0.5f,
                        )
                    )
                }

                /* Scientific name + Message not in db */
                Column(
                    modifier = Modifier
                        .padding(
                            top = 0.dp,
                            start = 10.dp,
                            end = 10.dp,
                            bottom = 0.dp,
                        )

                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontStyle = Italic,
                    )

                    Text(
                        text = "Todavía no tenemos usos de esta planta en nuestros registros",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(20.dp)
                        )
                        {
                            Markdown(
                                "¿Te gustaría contribuir a que _${name}_ esté en DruidNet?\n\n\n[Envíanos una lechuza mensajera](mailto:druidnetbeta@gmail.com) 🦉",
                                modifier = Modifier,
                                typography = markdownTypography(
                                    paragraph =
                                        MaterialTheme.typography.titleSmall
                                            .copy(
                                                textAlign = TextAlign.Center,
                                                fontSize = 18.sp
                                            ),
                                    link = MaterialTheme.typography.titleSmall
                                        .copy(
                                            textAlign = TextAlign.Center,
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

                    Spacer(modifier = Modifier.height(16.dp))

                }

            }

}

@Preview(showBackground = true)
@Composable
fun NotInDatabaseScreenPreview() {
    val name = "Quercus ilex"
    val score = 0.87
    val plantNetImageURL = "https://bs.plantnet.org/image/o/a9e693a35121b113f5a349b139260c685dc4bf0b"
    NotInDatabaseScreen(name, score, plantNetImageURL)
}

@Composable
fun SimilarPlantCard(
    plantResult: PlantResult,
    onClickSimilarPlantCard: (String, Double) -> Unit
) {
    val plantName = plantResult.species?.scientificNameWithoutAuthor ?: "Planta similar"
    val imgURL = plantResult.images?.first()?.url?.m

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
        onClick = { onClickSimilarPlantCard(plantName, plantResult.score ?: 0.0) },
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
                AsyncImage(
                    model = imgURL,
                    contentDescription = "Image for $plantName",
                    fallback = painterResource(R.drawable.grass),
                    placeholder = forwardingPainter(
                        painter = painterResource(R.drawable.eco),
                        colorFilter = ColorFilter.tint(Color.Gray),
                        alpha = 0.5f,
                    ),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = plantName,
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

/*
@Preview(showBackground = true)
@Composable
fun SuccessScreenPreview() {
    // 1. Create a dummy 'Plant' object for the most likely result.
    val dummyPlant = PlantsDataSource.loadPlants()[0]

    // 2. Create a dummy list of 'PlantResult' for similar plants.
    val dummySpeciesInfo2 = SpeciesInfo(
        scientificNameWithoutAuthor = "Papaver rhoeas",
        scientificName = "Papaver rhoeas L.",
        commonNames = listOf("Amapola común"),
    )
    val dummySpeciesImages2 = listOf(PlantImage(url = ImageUrls(m= "/home/akronix/Pictures/tanaceto_150_150.jpeg")))

    val dummyPlantResult2 = PlantResult(score = 0.72, species = dummySpeciesInfo2, images = dummySpeciesImages2)

    val dummySpeciesInfo3 = SpeciesInfo(
        scientificNameWithoutAuthor = "Eschscholzia caespitosa",
        scientificName = "Eschscholzia caespitosa Benth.",
        commonNames = listOf("Amapola de mechón")
    )
    val dummySpeciesImages3 = listOf(PlantImage(url = ImageUrls(m= "/home/akronix/Pictures/tanaceto_600_600.jpeg")))
    val dummyPlantResult3 = PlantResult(score = 0.65, species = dummySpeciesInfo3, images = dummySpeciesImages3)

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
}*/
