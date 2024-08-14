package org.druidanet.druidnetbeta.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.druidanet.druidnetbeta.R
import org.druidanet.druidnetbeta.data.PlantsDataSource
import org.druidanet.druidnetbeta.model.Plant
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme


@Composable
fun PlantSheetScreen(plant: Plant, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier
                .padding(0.dp)
    ) {
        Box(
            modifier = Modifier
                .height(250.dp)
                .padding(0.dp)
        ){
            Image(
                contentScale = ContentScale.FillWidth,
                painter = painterResource(plant.imageResourceId),
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

            Text(plant.latinName,
                fontStyle = Italic,
                style = MaterialTheme.typography.headlineLarge,
            )
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(10.dp)
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.datasheet_common_names))
                Text(plant.commonNames.toString())
            }
            Text(
                stringResource(R.string.datasheet_usages_literal),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}



/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
@Preview(showBackground = true)
@Composable
fun PlantSheetScreenPreview() {
    DruidNetBetaTheme {
        PlantSheetScreen(PlantsDataSource.loadPlants()[2],
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
