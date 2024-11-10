package org.druidanet.druidnet

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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import org.druidanet.druidnet.data.DruidNetUiState
import org.druidanet.druidnet.ui.AboutScreen
import org.druidanet.druidnet.ui.CatalogScreen
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.PlantSheetBottomBar
import org.druidanet.druidnet.ui.PlantSheetScreen
import org.druidanet.druidnet.ui.WelcomeScreen


enum class Screen(@StringRes val title: Int) {
    Welcome(title = R.string.app_name),
    Catalog(title = R.string.title_screen_catalog),
    PlantSheet(title = R.string.title_screen_plant_sheet),
    About(title = R.string.title_screen_about)
}

@Composable
fun DruidNetApp(
    viewModel: DruidNetViewModel = viewModel( factory = DruidNetViewModel.factory ),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(backStackEntry?.destination?.route ?: Screen.Welcome.name)

    val plantList by viewModel.getAllPlants().collectAsState(emptyList())

    val firstLaunch by viewModel.isFirstLaunch().collectAsState(false)

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
            currentSection = druidNetUiState.currentSection,
            hasConfusions = druidNetUiState.plantHasConfusions
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
                    onNavigationButtonClick = {screen: Screen ->
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
                    onChangeSection = { section -> { viewModel.changeSection(section) } },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            composable(route = Screen.About.name) {
                AboutScreen(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

        }

        // Show disclaimer Dialog if it's first launch of the app by the user
        if (firstLaunch) {
            Disclaimer(
                onDismissDisclaimer = { },
                onAcceptDisclaimer = { viewModel.unsetFirstLaunch() } )
        }

    }
}

@Composable
fun Disclaimer(
    onDismissDisclaimer: () -> Unit,
    onAcceptDisclaimer: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Icono de aviso")
        },
        title = {
            Text(text = "Soy una guía")
        },
        text = {
            Text(text =
                    "El fin de esta app es ayudarte a conocer los usos tradicionales de las plantas de nuestro entorno.\n" +
                    "Es tu responsabilidad la identificación precisa y el consumo que hagas de las mismas.")
        },
        onDismissRequest = {
            onDismissDisclaimer()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAcceptDisclaimer()
                }
            ) {
                Text(stringResource(R.string.dialog_accept_disclaimer))
            }
        },

    )
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
        var topBarIconPath: Int? = null
        val topBarTitle: String

        when (currentScreen) {
            Screen.Welcome -> return {} // No top bar in Welcome screen
            Screen.Catalog -> {
                topBarIconPath = R.drawable.menu_book
                topBarTitle = stringResource(currentScreen.title)
            }
            Screen.About -> {
                topBarTitle = stringResource(currentScreen.title)
            }
            Screen.PlantSheet -> {
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

