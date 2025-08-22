package org.druidanet.druidnet.navigation

/**
 * Interface to describe the navigation destinations for the app
 */
abstract class NavigationDestination {

    /**
     * Unique name to define the path for a composable
     */
    abstract val route: String

    /**
     * String resource id to that contains title to be displayed for the screen.
     */
    abstract val title: Int

    open val hasTopBar: Boolean = true

    open val topBarIconPath: Int? = null
}