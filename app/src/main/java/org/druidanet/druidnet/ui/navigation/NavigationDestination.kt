/**
 * Interface to describe the navigation destinations for the app
 */
interface Screen {
    /**
     * Unique name to define the path for a composable
     */
    val route: String

    /**
     * String resource id to that contains title to be displayed for the screen.
     */
    val title: Int
}