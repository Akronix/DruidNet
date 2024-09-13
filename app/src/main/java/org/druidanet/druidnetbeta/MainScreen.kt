package org.druidanet.druidnetbeta

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.druidanet.druidnetbeta.data.DruidNetUiState
import org.druidanet.druidnetbeta.ui.CatalogScreen
import org.druidanet.druidnetbeta.ui.DruidNetViewModel
import org.druidanet.druidnetbeta.ui.PlantSheetBottomBar
import org.druidanet.druidnetbeta.ui.PlantSheetScreen
import org.druidanet.druidnetbeta.ui.WelcomeScreen


enum class Screen(@StringRes val title: Int) {
    Welcome(title = R.string.app_name),
    Catalog(title = R.string.title_screen_catalog),
    PlantSheet(title = R.string.title_screen_plant_sheet),
}

@Composable
fun DruidNetApp(
    viewModel: DruidNetViewModel = viewModel( factory = DruidNetViewModel.factory ),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(backStackEntry?.destination?.route ?: Screen.Welcome.name)
    val plantList by viewModel.getAllPlants().collectAsState(emptyList())

    val druidNetUiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    //canNavigateBack = navController.previousBackStackEntry != null,

    Scaffold(
        topBar = DruidNetAppBar(
            currentScreen = currentScreen,
            navigateUp = { navController.navigateUp() },
            uiState = druidNetUiState
        ),
        bottomBar = PlantSheetBottomBar(
            currentScreen = currentScreen,
            onClickBottomNavItem = { section -> { viewModel.changeSection(section) } },
            currentSection = druidNetUiState.currentSection
        ),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.name,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable(route = Screen.Welcome.name) {
                WelcomeScreen(
                    onCatalogButtonClick = {
                        navController.navigate(Screen.Catalog.name)
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
                            viewModel.updatePlantUi(plant.plantId)
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
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

        }
    }
}

@SuppressLint("ComposableNaming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DruidNetAppBar(
    currentScreen: Screen,
    navigateUp: () -> Unit,
    uiState: DruidNetUiState,
    modifier: Modifier = Modifier
): @Composable () -> Unit {
        val topBarIconPath: Int?
        val topBarTitle: String

        when (currentScreen) {
            Screen.Welcome -> return {} // No top bar in Welcome screen
            Screen.Catalog -> {
                topBarIconPath = R.drawable.menu_book
                topBarTitle = stringResource(currentScreen.title)
            }
            Screen.PlantSheet -> {
                topBarIconPath = null
                topBarTitle = uiState.plantUiState!!.displayName
            }
        }

        return {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (topBarIconPath != null) {
                            Image(
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.bar_image_size))
                                    .padding(dimensionResource(id = R.dimen.padding_small)),
                                painter = painterResource(topBarIconPath),
                                contentDescription = null
                            )
                        }

                        Text(
                            text = topBarTitle,
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                modifier = modifier
            )
        }
    }

