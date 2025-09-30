package org.druidanet.druidnet.navigation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.ui.DruidNetViewModel
import org.druidanet.druidnet.ui.identify.CameraScreen
import org.druidanet.druidnet.ui.identify.IdentifyScreen
import org.druidanet.druidnet.ui.plant_sheet.PlantSheetScreen
import org.druidanet.druidnet.ui.screens.AboutScreen
import org.druidanet.druidnet.ui.screens.BibliographyScreen
import org.druidanet.druidnet.ui.screens.CatalogScreen
import org.druidanet.druidnet.ui.screens.CreditsScreen
import org.druidanet.druidnet.ui.screens.GlossaryScreen
import org.druidanet.druidnet.ui.screens.RecomendationsScreen
import org.druidanet.druidnet.ui.screens.WelcomeScreen

object WelcomeDestination : NavigationDestination() {
    override val route = "welcome"
    override val title = R.string.app_name
    override val hasTopBar = false
}

object CameraDestination : NavigationDestination() {
    override val route = "camera"
    override val title = R.string.app_name
    override val hasTopBar = false
}

object IdentifyDestination : NavigationDestination() {
    override val route = "identify"
    override val title = R.string.title_screen_identify
}

object CatalogDestination : NavigationDestination() {
    override val route = "catalog"
    override val title = R.string.title_screen_catalog
    override val topBarIconPath = R.drawable.menu_book
    override val hasTopBar = false
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

@Serializable
object PlantSheetDestination : NavigationDestination() {
    override val route = "plant_sheet"
    override val title = R.string.title_screen_plant_sheet
    const val plantArg = "plantLatinName"
    val routeWithArgs = "$route/{$plantArg}"
    override val hasTopBar = false
}

// We should upgrade to type-safe navigation: https://developer.android.com/guide/navigation/design/type-safety
//@Serializable
//data class PlantSheetDestination(val plantId: Int = 0) : Screen {
//    override val route = "plant_sheet"
//    override val title = R.string.title_screen_plant_sheet
//    val routeWithArgs = "$route/{$plantId}"
//}

val screensByRoute : Map<String, NavigationDestination> =
    mapOf(
        WelcomeDestination.route to WelcomeDestination,
        CatalogDestination.route to CatalogDestination,
        PlantSheetDestination.routeWithArgs to PlantSheetDestination,
        AboutDestination.route to AboutDestination,
        BibliographyDestination.route to BibliographyDestination,
        CreditsDestination.route to CreditsDestination,
        RecommendationsDestination.route to RecommendationsDestination,
        GlossaryDestination.route to GlossaryDestination,
        IdentifyDestination.route to IdentifyDestination,
        CameraDestination.route to CameraDestination,
    )

// Before Implementation:
//enum class Screen(@StringRes val title: Int) {
//    Welcome(title = R.string.app_name),
//    Catalog(title = R.string.title_screen_catalog),
//    PlantSheet(title = R.string.title_screen_plant_sheet),
//    About(title = R.string.title_screen_about),
//    Bibliography(title = R.string.title_screen_bibliography),
//    Credits(title = R.string.title_screen_credits),
//}


@Composable
fun DruidNetNavHost(
    navController: NavHostController,
    viewModel: DruidNetViewModel,
    innerPadding: PaddingValues,
    bibliographyStr: String,
    scrollStateCatalog: LazyListState,
    scrollStateGlossary: ScrollState
) {
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
        composable( route = CameraDestination.route) {
            CameraScreen(
                goToResultsScreen = { navController.navigate(IdentifyDestination.route) }
            )
        }
        composable( route = IdentifyDestination.route) {
            IdentifyScreen(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            )
        }
        composable(route = CatalogDestination.route) {
            CatalogScreen(
                onClickPlantCard = { plant ->
                    navController.navigate("${PlantSheetDestination.route}/${plant.latinName}")
                },
                listState = scrollStateCatalog,
                viewModel = viewModel,
                navigateBack = { navController.navigateUp() },
                innerPadding = innerPadding,
                modifier = Modifier
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
                        navigateBack = { navController.navigateUp() },
                        innerPadding = innerPadding,
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
                scrollState = scrollStateGlossary,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        }
    }
}
