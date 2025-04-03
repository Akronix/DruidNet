package org.druidanet.druidnet.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.druidanet.druidnet.ui.AboutScreen
import org.druidanet.druidnet.ui.BibliographyScreen
import org.druidanet.druidnet.ui.CatalogScreen
import org.druidanet.druidnet.ui.CreditsScreen
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.PlantSheetScreen
import org.druidanet.druidnet.ui.WelcomeScreen

/**
 * Provides Navigation graph for the application.
 */
/*
@Composable
fun DruidNetNavHost(
    navController: NavHostController,
    viewModel: DruidNetViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.name,
        modifier = Modifier
            .padding(innerPadding)
    ) {
        composable(route = Screen.Welcome.name) {
            WelcomeScreen(
                onNavigationButtonClick = { screen: Screen ->
                    navController.navigate(screen.name)
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
        composable(route = Screen.Catalog.name) {
            CatalogScreen(
                plantList = plantList,
                onClickPlantCard = { plant ->
                    viewModel.setSelectedPlant(plant.plantId)
                    coroutineScope.launch {
                        viewModel.updatePlantUi(plant.plantId, plant.displayName)
                        navController.navigate(Screen.PlantSheet.name)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
        composable(route = Screen.PlantSheet.name) {
            PlantSheetScreen(
                plant = druidNetUiState.plantUiState!!,
                currentSection = druidNetUiState.currentSection,
                onChangeSection = { section -> { viewModel.changeSection(section) } },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        composable(route = Screen.About.name) {
            AboutScreen(
                onNavigationButtonClick = { screen: Screen ->
                    navController.navigate(screen.name)
                },
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        composable(route = Screen.Bibliography.name) {
            BibliographyScreen(
                bibliographyStr = bibliographyStr,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        composable(route = Screen.Credits.name) {
            CreditsScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        }

    }
}
*/