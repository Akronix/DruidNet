package org.druidanet.druidnet

import Screen
import android.annotation.SuppressLint
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.druidanet.druidnet.data.DruidNetUiState
import org.druidanet.druidnet.ui.screens.AboutScreen
import org.druidanet.druidnet.ui.screens.BibliographyScreen
import org.druidanet.druidnet.ui.screens.CatalogScreen
import org.druidanet.druidnet.ui.screens.CreditsScreen
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.screens.PlantSheetBottomBar
import org.druidanet.druidnet.ui.screens.PlantSheetScreen
import org.druidanet.druidnet.ui.screens.WelcomeScreen

object WelcomeDestination : Screen {
    override val route = "welcome"
    override val title = R.string.app_name
}

object CatalogDestination : Screen {
    override val route = "catalog"
    override val title = R.string.title_screen_catalog
}

object AboutDestination : Screen {
    override val route = "about"
    override val title = R.string.title_screen_about
}

object BibliographyDestination : Screen {
    override val route = "bibliography"
    override val title = R.string.title_screen_bibliography
}

object CreditsDestination : Screen {
    override val route = "credits"
    override val title = R.string.title_screen_credits
}

// We should upgrade to type-safe navigation: https://developer.android.com/guide/navigation/design/type-safety
//@Serializable
//data class PlantSheetDestination(val plantId: Int = 0) : Screen {
//    override val route = "plant_sheet"
//    override val title = R.string.title_screen_plant_sheet
//    val routeWithArgs = "$route/{$plantId}"
//}

object PlantSheetDestination : Screen {
    override val route = "plant_sheet"
    override val title = R.string.title_screen_plant_sheet
    const val plantArg = "plantId"
    val routeWithArgs = "$route/{$plantArg}"
}

val screensByRoute : Map<String, Screen> =
    mapOf(
        WelcomeDestination.route to WelcomeDestination,
        CatalogDestination.route to CatalogDestination,
        PlantSheetDestination.routeWithArgs to PlantSheetDestination,
        AboutDestination.route to AboutDestination,
        BibliographyDestination.route to BibliographyDestination,
        CreditsDestination.route to CreditsDestination
    )


//enum class Screen(@StringRes val title: Int) {
//    Welcome(title = R.string.app_name),
//    Catalog(title = R.string.title_screen_catalog),
//    PlantSheet(title = R.string.title_screen_plant_sheet),
//    About(title = R.string.title_screen_about),
//    Bibliography(title = R.string.title_screen_bibliography),
//    Credits(title = R.string.title_screen_credits),
//}

@Composable
fun DruidNetApp(
    viewModel: DruidNetViewModel = viewModel( factory = DruidNetViewModel.factory ),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen : Screen = screensByRoute[backStackEntry?.destination?.route] ?: WelcomeDestination

    val plantList by viewModel.getAllPlants().collectAsState(emptyList())
    val bibliography by viewModel.getBibliography().collectAsState(emptyList())
    val bibliographyStr = if (bibliography.isNotEmpty())
                bibliography.map { "* " + it.toMarkdownString() }
                            .reduce { acc : String, ref: String -> "$acc\n$ref"} else ""

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
            startDestination = WelcomeDestination.route,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable(route = WelcomeDestination.route) {
                WelcomeScreen(
                    onNavigationButtonClick = {screen: Screen ->
                        navController.navigate(screen.route)
                    },
                    updateDatabase = {viewModel.getDatabaseUpdate()},
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
            composable(route = CatalogDestination.route) {
                CatalogScreen(
                    plantList = plantList,
                    onClickPlantCard = { plant ->
                        viewModel.setSelectedPlant(plant.plantId)
                        coroutineScope.launch {
                            viewModel.updatePlantUi(plant.plantId, plant.displayName)
                            navController.navigate("${PlantSheetDestination.route}/${plant.plantId}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
            composable(route = PlantSheetDestination.routeWithArgs,
                        arguments = listOf(navArgument(PlantSheetDestination.plantArg){
                        type = NavType.IntType
                        })
            ){

                PlantSheetScreen(
                    plant = druidNetUiState.plantUiState!!,
                    currentSection = druidNetUiState.currentSection,
                    onChangeSection = { section -> { viewModel.changeSection(section) } },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            composable(route = AboutDestination.route) {
                AboutScreen(
                    onNavigationButtonClick = {screen: Screen ->
                        navController.navigate(screen.route)
                    },
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            composable(route = BibliographyDestination.route) {
                BibliographyScreen(
                    bibliographyStr = bibliographyStr,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            composable(route = CreditsDestination.route) {
                CreditsScreen(
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
                    "Es tu responsabilidad la identificación precisa y el empleo que hagas de las mismas.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp))
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
                Text(stringResource(R.string.dialog_accept_disclaimer),
                    style = MaterialTheme.typography.labelMedium)
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

        if (currentScreen == WelcomeDestination) return {} // No top bar in Welcome screen

        val topBarTitle: String = when (currentScreen) {
            PlantSheetDestination -> uiState.plantUiState!!.displayName
            else -> stringResource(currentScreen.title)
        }

        val topBarIconPath: Int? = if (currentScreen == CatalogDestination) R.drawable.menu_book else null

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
                            overflow = TextOverflow.Ellipsis,
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

