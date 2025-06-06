package org.druidanet.druidnet.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.druidanet.druidnet.CatalogDestination
import org.druidanet.druidnet.DruidNetAppBar
import org.druidanet.druidnet.R
import org.druidanet.druidnet.data.plant.PlantsDataSource
import org.druidanet.druidnet.model.PlantCard
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
    modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(plantsList) { plant ->
            PlantCard(
                plant = plant,
                onClickPlantCard = onClickPlantCard,
                modifier = Modifier.padding(8.dp)
            )

        }

    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CatalogScreen(
    plantList: List<PlantCard>,
    onClickPlantCard: (PlantCard) -> Unit,
    navigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = DruidNetAppBar(
            navigateUp = navigateBack,
            topBarTitle = stringResource(CatalogDestination.title),
            topBarIconPath = R.drawable.menu_book,
        ),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding)
        ) {
            PlantsList(
                plantsList = plantList,
                onClickPlantCard = onClickPlantCard
            )
        }
    }

}

/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
@Preview(showBackground = true)
@Composable
fun CatalogPreview() {
    DruidNetTheme {
        CatalogScreen(
            plantList = PlantsDataSource.loadPlants()
                .map { PlantCard(it.plantId, it.displayName, it.imagePath, it.latinName, false) },
            onClickPlantCard = { },
            snackbarHostState = SnackbarHostState(),
            navigateBack = {},
        )
    }
}

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
