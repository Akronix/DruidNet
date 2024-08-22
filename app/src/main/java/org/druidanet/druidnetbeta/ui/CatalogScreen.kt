package org.druidanet.druidnetbeta.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.druidanet.druidnetbeta.data.PlantsDataSource
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme



@Composable
fun PlantCard(plant: Plant, onClickPlantCard: (Plant) -> Unit, modifier: Modifier) {
    Card(
        onClick = { onClickPlantCard(plant) },
        modifier = modifier
    ) {
        Column {
            Image(
                painter = painterResource(plant.imageResourceId),
                contentDescription = "Image for {plant.displayName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = plant.displayName,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

        }
    }
}

@Composable
fun PlantsList(
    plantsList: List<Plant>,
    onClickPlantCard: (Plant) -> Unit,
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

@Composable
fun CatalogScreen(
    plantList: List<Plant>,
    onClickPlantCard: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        PlantsList(
            plantsList = plantList,
            onClickPlantCard = onClickPlantCard
        )
    }
}

/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
@Preview(showBackground = true)
@Composable
fun CatalogPreview() {
    DruidNetBetaTheme {
        CatalogScreen(plantList = PlantsDataSource.loadPlants(), onClickPlantCard = { })
    }
}

/**
 * Composable that displays what the UI of the app looks like in dark theme in the design tab.
 */
//@Preview
//@Composable
//fun CatalogDarkThemePreview() {
//    DruidNetBetaTheme(darkTheme = true) {
//        CatalogScreen()
//    }
//}
