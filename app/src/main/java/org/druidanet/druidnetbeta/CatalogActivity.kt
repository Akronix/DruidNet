package org.druidanet.druidnetbeta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.data.PlantsDataSource
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DruidNetBetaTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    Catalog(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PlantCard(plant: Plant, modifier: Modifier) {
    Card(modifier = modifier) {
        Column{
            Image(
                painter = painterResource(plant.imageResourceId),
                contentDescription = "Image for {plant.latinName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = plant.latinName,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )

        }
    }
}

@Composable
fun PlantsList(plantsList: List<Plant>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(plantsList) { plant ->
            PlantCard(
                plant = plant,
                modifier = Modifier.padding(8.dp)
            )

        }

    }

}

@Composable
fun Catalog(modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(layoutDirection),
            ),
    ) {
        PlantsList(
            plantsList = PlantsDataSource().loadPlants(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    DruidNetBetaTheme {
        Catalog()
    }
}