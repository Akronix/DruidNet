package org.druidanet.druidnetbeta.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.druidanet.druidnetbeta.data.PlantsDataSource
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme


@Composable
fun PlantSheetScreen(plant: Plant?, modifier: Modifier = Modifier) {
    Surface (
        modifier = modifier
    ) {
        Text(plant?.displayName!!)
    }
}



/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
@Preview(showBackground = true)
@Composable
fun PlantSheetScreenPreview() {
    DruidNetBetaTheme {
        PlantSheetScreen(PlantsDataSource.loadPlants()[1],
        modifier = Modifier.fillMaxSize()
        )
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
