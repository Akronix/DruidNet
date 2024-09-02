package org.druidanet.druidnetbeta.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.druidanet.druidnetbeta.R
import org.druidanet.druidnetbeta.Screen
import org.druidanet.druidnetbeta.data.PlantsDataSource
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme

@SuppressLint("ComposableNaming")
@Composable
fun PlantSheetBottomBar(
    currentScreen: Screen,
    onClickBottomNavItem: (PlantSheetSection) -> () -> Unit,
    currentSection: PlantSheetSection
) : @Composable () -> Unit {
    if (currentScreen == Screen.PlantSheet)
        return {
            BottomAppBar(
                actions = {

                    SectionButton(
                        rowScope = this,
                        resImg = R.drawable.description,
                        resText = stringResource(R.string.descp_plantsheet_bottombar),
                        onClick = onClickBottomNavItem(PlantSheetSection.DESCRIPTION),
                        selected = currentSection == PlantSheetSection.DESCRIPTION
                    )

                    SectionButton(
                        rowScope = this,
                        resImg = R.drawable.confusions,
                        resText = stringResource(R.string.confusions_plantsheet_bottombar),
                        onClick = onClickBottomNavItem(PlantSheetSection.CONFUSIONS),
                        selected = currentSection == PlantSheetSection.CONFUSIONS
                    )

                    SectionButton(
                        rowScope = this,
                        resImg = R.drawable.usages,
                        resText = stringResource(R.string.usages_plantsheet_bottombar),
                        onClick = onClickBottomNavItem(PlantSheetSection.USAGES),
                        selected = currentSection == PlantSheetSection.USAGES
                    )
                }
            )
        }
    else
        return {}
}

@Composable
fun SectionButton(rowScope: RowScope,
                  resImg: Int, resText: String,
                  onClick: () -> Unit,
                  selected: Boolean) {
    rowScope.NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painterResource(id = resImg),
            "Move to ${resText} section",
                modifier = Modifier.size(dimensionResource(R.dimen.section_buttom_img))
        )},
//    modifier: Modifier = Modifier,
    label = { Text (resText) },
    alwaysShowLabel = true,
    )

}

/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
@Preview(showBackground = true)
@Composable
fun PlantSheetBottomBarPreview() {
    DruidNetBetaTheme {
        Scaffold(
            bottomBar = PlantSheetBottomBar(
                Screen.PlantSheet,
                onClickBottomNavItem = { section -> {  } },
                currentSection = PlantSheetSection.DESCRIPTION
            ),
        ) {
            PlantSheetScreen(
                plant = PlantsDataSource.loadPlants()[0],
                modifier = Modifier.padding(it)
            )
        }
    }
}
