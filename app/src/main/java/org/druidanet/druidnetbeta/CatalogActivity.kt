@file:OptIn(ExperimentalMaterial3Api::class)

package org.druidanet.druidnetbeta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.druidanet.druidnetbeta.data.PlantsDataSource
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DruidNetBetaTheme {
                CatalogAndBar()
            }
        }
    }

    @Composable
    fun PlantCard(plant: Plant, modifier: Modifier) {
        Card(modifier = modifier) {
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
    fun CatalogAppBar(modifier: Modifier = Modifier) {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.bar_image_size))
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                        painter = painterResource(R.drawable.menu_book),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.catalog_bar_title),
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            },
            modifier = modifier
        )
    }

    @Composable
    fun Catalog(modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier
        ) {
            PlantsList(
                plantsList = PlantsDataSource.loadPlants()
            )
        }
    }


    @Composable
    fun CatalogAndBar() {
        Scaffold(
            topBar = {
                CatalogAppBar()
            }
        ) { innerPadding ->
            Catalog(
                modifier = Modifier.padding(innerPadding)
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
            CatalogAndBar()
        }
    }

    /**
     * Composable that displays what the UI of the app looks like in dark theme in the design tab.
     */
//    @Preview
//    @Composable
//    fun CatalogDarkThemePreview() {
//        DruidNetBetaTheme(darkTheme = true) {
//            CatalogAndBar()
//        }
//    }

}