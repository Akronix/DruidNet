package org.druidanet.druidnetbeta

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.druidanet.druidnetbeta.ui.CatalogAndBar
import org.druidanet.druidnetbeta.ui.WelcomeScreen


enum class Screen(@StringRes val title: Int) {
    Welcome(title = R.string.app_name),
    Catalog(title = R.string.title_screen_catalog),
}

@Composable
fun DruidNetApp(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

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
                CatalogAndBar(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
    }
}
