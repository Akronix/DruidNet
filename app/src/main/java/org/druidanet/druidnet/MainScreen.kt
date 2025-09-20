package org.druidanet.druidnet

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.druidanet.druidnet.navigation.DruidNetNavHost
import org.druidanet.druidnet.navigation.NavigationDestination
import org.druidanet.druidnet.navigation.WelcomeDestination
import org.druidanet.druidnet.navigation.screensByRoute
import org.druidanet.druidnet.ui.DruidNetViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DruidNetApp(
    viewModel: DruidNetViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    // Use the screensByRoute from the navigation package
    val currentNavigationDestination : NavigationDestination = screensByRoute[backStackEntry?.destination?.route] ?: WelcomeDestination

    val bibliography by viewModel.getBibliography().collectAsState(emptyList())
    val bibliographyStr = if (bibliography.isNotEmpty())
                bibliography.map { "* " + it.toMarkdownString() }
                            .reduce { acc : String, ref: String -> "$acc\n$ref"} else ""

    val firstLaunch by viewModel.isFirstLaunch().collectAsState(false)

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var justStartedApp by remember { mutableStateOf(true)}

    val scrollStateCatalog = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState(0, 0)
    }

    val scrollStateGlossary = rememberSaveable(saver = ScrollState.Saver) {
        ScrollState(initial = 0) // Initialize a new ScrollState
    }

    val appMainTopBar: @Composable () -> Unit = if (currentNavigationDestination.hasTopBar) {
        {DruidNetAppBar(
            topBarTitle = stringResource(currentNavigationDestination.title),
            navigateUp = { navController.navigateUp() },
            topBarIconPath = currentNavigationDestination.topBarIconPath,
        )}
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
                ),
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        DruidNetNavHost(
            navController = navController,
            viewModel = viewModel,
            innerPadding = innerPadding,
            bibliographyStr = bibliographyStr,
            scrollStateCatalog = scrollStateCatalog,
            scrollStateGlossary = scrollStateGlossary
        )

        // Run database check & update when the app starts
        if (justStartedApp) {
            LaunchedEffect(Unit) {
                justStartedApp = false
                viewModel.checkAndUpdateDatabase()
            }
        }

        // Show Snackbar when a message is available
        LaunchedEffect(snackbarMessage) {
            snackbarMessage?.let { message ->
                launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                    // After the snackbar is shown and dismissed, clear the message.
                    viewModel.onSnackbarMessageShown()
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DruidNetAppBar(
    topBarTitle: String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    topBarIconPath: Int? = null,
    thumbnail: ImageBitmap? = null,
    topBarColor: Color = Color.Unspecified,
    onActionClick: () -> Unit = {},
    actionIconContentDescription: String? = null,
    actionIcon: ImageVector? = null
) {

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
                        } else if (thumbnail != null) {
                                Image(
                                    bitmap = thumbnail,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(dimensionResource(id = R.dimen.bar_image_size))
                                        .padding(dimensionResource(id = R.dimen.padding_small))
                                        .clip(CircleShape)
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
