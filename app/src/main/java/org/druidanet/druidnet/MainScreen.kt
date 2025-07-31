package org.druidanet.druidnet

import NavigationDestination
import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.plant_sheet.PlantSheetScreen
import org.druidanet.druidnet.ui.screens.AboutScreen
import org.druidanet.druidnet.ui.screens.BibliographyScreen
import org.druidanet.druidnet.ui.screens.CatalogScreen
import org.druidanet.druidnet.ui.screens.CreditsScreen
import org.druidanet.druidnet.ui.screens.WelcomeScreen
import org.druidanet.druidnet.ui.screens.GlossaryScreen
import org.druidanet.druidnet.ui.screens.RecomendationsScreen


object WelcomeDestination : NavigationDestination() {
    override val route = "welcome"
    override val title = R.string.app_name
    override val hasTopBar = false
}

object CatalogDestination : NavigationDestination() {
    override val route = "catalog"
    override val title = R.string.title_screen_catalog
    override val topBarIconPath = R.drawable.menu_book
}

object AboutDestination : NavigationDestination() {
    override val route = "about"
    override val title = R.string.title_screen_about
}

object BibliographyDestination : NavigationDestination() {
    override val route = "bibliography"
    override val title = R.string.title_screen_bibliography
}

object CreditsDestination : NavigationDestination() {
    override val route = "credits"
    override val title = R.string.title_screen_credits
}

object RecommendationsDestination : NavigationDestination() {
    override val route = "recommendations"
    override val title = R.string.title_screen_recommendations
}

object GlossaryDestination : NavigationDestination() {
    override val route = "glossary"
    override val title = R.string.title_screen_glossary
    override val topBarIconPath = R.drawable.dictionary
}

// We should upgrade to type-safe navigation: https://developer.android.com/guide/navigation/design/type-safety
//@Serializable
//data class PlantSheetDestination(val plantId: Int = 0) : Screen {
//    override val route = "plant_sheet"
//    override val title = R.string.title_screen_plant_sheet
//    val routeWithArgs = "$route/{$plantId}"
//}

@Serializable
object PlantSheetDestination : NavigationDestination() {
    override val route = "plant_sheet"
    override val title = R.string.title_screen_plant_sheet
    const val plantArg = "plantLatinName"
    val routeWithArgs = "$route/{$plantArg}"
    override val hasTopBar = false
}

val screensByRoute : Map<String, NavigationDestination> =
    mapOf(
        WelcomeDestination.route to WelcomeDestination,
        CatalogDestination.route to CatalogDestination,
        PlantSheetDestination.routeWithArgs to PlantSheetDestination,
        AboutDestination.route to AboutDestination,
        BibliographyDestination.route to BibliographyDestination,
        CreditsDestination.route to CreditsDestination,
        RecommendationsDestination.route to RecommendationsDestination,
        GlossaryDestination.route to GlossaryDestination
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
    val currentNavigationDestination : NavigationDestination = screensByRoute[backStackEntry?.destination?.route] ?: WelcomeDestination

    val plantList by viewModel.getAllPlants().collectAsState(emptyList())
    val bibliography by viewModel.getBibliography().collectAsState(emptyList())
    val bibliographyStr = if (bibliography.isNotEmpty())
                bibliography.map { "* " + it.toMarkdownString() }
                            .reduce { acc : String, ref: String -> "$acc\n$ref"} else ""

    val firstLaunch by viewModel.isFirstLaunch().collectAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }

    var justStartedApp by remember { mutableStateOf(true)}

    val scrollStateCatalog = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState(0, 0)
    }

    val scrollStateGlossary = rememberSaveable(saver = ScrollState.Saver) {
        ScrollState(initial = 0) // Initialize a new ScrollState
    }

    //canNavigateBack = navController.previousBackStackEntry != null,

    val appMainTopBar: @Composable () -> Unit = if (currentNavigationDestination.hasTopBar) {
        if (currentNavigationDestination.route == "catalog") {
            DruidNetAppBar(
                topBarTitle = stringResource(currentNavigationDestination.title),
                navigateUp = { navController.navigateUp() },
                topBarIconPath = currentNavigationDestination.topBarIconPath,
                actionIcon = Icons.Rounded.Search,
                actionIconContentDescription = stringResource(R.string.appbar_search_button),
                onActionClick = showSearchToolbar()
            )
        } else {
            DruidNetAppBar(
                topBarTitle = stringResource(currentNavigationDestination.title),
                navigateUp = { navController.navigateUp() },
                topBarIconPath = currentNavigationDestination.topBarIconPath
            )
        }
    } else { {} }

    Scaffold(
        topBar = appMainTopBar,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.exclude(
                        WindowInsets.ime,
                    ),
                ),)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WelcomeDestination.route,
        ) {
            composable(route = WelcomeDestination.route) {
                    WelcomeScreen(
                        onNavigationButtonClick = { navigationDestination: NavigationDestination ->
                            navController.navigate(navigationDestination.route)
                        },
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
                        navController.navigate("${PlantSheetDestination.route}/${plant.latinName}")
                    },
                    listState = scrollStateCatalog,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
            composable(
                route = PlantSheetDestination.routeWithArgs,
                arguments = listOf(navArgument(PlantSheetDestination.plantArg) {
                    type = NavType.StringType
                }),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "druidnet://druidanet.org/plant_sheet/{plantLatinName}"
                    }
                )
            ) {
                val plantLatinName: String? = it.arguments?.getString("plantLatinName")

                if (plantLatinName != null) {

                    val defaultUriHandler = LocalUriHandler.current
                    CompositionLocalProvider(LocalUriHandler provides object : UriHandler {
                        override fun openUri(uri: String) {
                            if (uri.startsWith("druidnet://")) {
                                println("TEST for url: $uri")
                                navController.navigate(uri.toUri())
                            } else if (uri.startsWith("plant_sheet/")) {
                                println("TEST for url: $uri")
                                navController.navigate(uri)
                            } else {
                                defaultUriHandler.openUri(uri)
                            }
                        }
                    }) {
                        PlantSheetScreen(
                            plantLatinName,
                            { navController.navigateUp() },
                            innerPadding,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }

                } else {
                    Text("Error: There's no plant reference in the route")
                }

            }
            composable(route = AboutDestination.route) {
                AboutScreen(
                    onNavigationButtonClick = { navigationDestination: NavigationDestination ->
                        navController.navigate(navigationDestination.route)
                    },
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            composable(route = BibliographyDestination.route) {
                BibliographyScreen(
                    bibliographyStr = bibliographyStr,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            composable(route = CreditsDestination.route) {
                CreditsScreen(
                    creditsText = viewModel.getCreditsText(),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            composable(route = RecommendationsDestination.route) {
                RecomendationsScreen(
                    recommendationsTxt = viewModel.getRecommendationsText(),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
            composable(route = GlossaryDestination.route) {
                GlossaryScreen(
                    glossaryTxt = viewModel.getGlossaryText(),
                    scrollStateGlossary,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }

        }

        // Run database check & update when the app starts
        if (justStartedApp) {
            LaunchedEffect(Unit) {
                justStartedApp = false
                viewModel.checkAndUpdateDatabase(snackbarHost = snackbarHostState)
            }
        }

        // Show disclaimer Dialog if it's first launch of the app by the user
        if (firstLaunch) {
            Disclaimer(
                onDismissDisclaimer = { },
                onAcceptDisclaimer = { viewModel.unsetFirstLaunch() })
        }
    }
}

fun showSearchToolbar(): () -> Unit {
    return {}
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
        onDismissRequest = onDismissDisclaimer,
        confirmButton = {
            TextButton(
                onClick = onAcceptDisclaimer

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
    topBarTitle: String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    topBarIconPath: Int? = null,
    topBarColor: Color = Color.Unspecified,
    onActionClick: () -> Unit = {},
    actionIconContentDescription: String? = null,
    actionIcon: ImageVector? = null
): @Composable () -> Unit {

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
                            color = topBarColor,
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
                actions = {
                    if (actionIcon != null && actionIconContentDescription != null) {
                        IconButton(onClick = onActionClick) {
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = actionIconContentDescription,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
                modifier = modifier
            )
        }
    }

