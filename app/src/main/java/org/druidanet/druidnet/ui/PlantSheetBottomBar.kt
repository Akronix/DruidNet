package org.druidanet.druidnet.ui

import android.annotation.SuppressLint
import android.graphics.Color.alpha
import androidx.collection.emptyLongSet
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.druidanet.druidnet.R
import org.druidanet.druidnet.Screen
import org.druidanet.druidnet.data.PlantsDataSource
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@SuppressLint("ComposableNaming")
@Composable
fun PlantSheetBottomBar(
    currentScreen: Screen,
    onClickBottomNavItem: (PlantSheetSection) -> () -> Unit,
    currentSection: PlantSheetSection,
    hasConfusions: Boolean
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

                    SectionBadgedButton(
                        rowScope = this,
                        resImg = R.drawable.confusions,
                        resText = stringResource(R.string.confusions_plantsheet_bottombar),
                        onClick = onClickBottomNavItem(PlantSheetSection.CONFUSIONS),
                        selected = currentSection == PlantSheetSection.CONFUSIONS,
                        showBadge = hasConfusions,
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
            "Move to $resText section",
                modifier = Modifier.size(dimensionResource(R.dimen.section_buttom_img))
        )},
//    modifier: Modifier = Modifier,
    label = { Text (resText) },
    alwaysShowLabel = true,
    )
}

@Composable
fun SectionBadgedButton(rowScope: RowScope,
                  resImg: Int, resText: String,
                  onClick: () -> Unit,
                  selected: Boolean,
                  showBadge: Boolean) {
    rowScope.NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            BadgedBox(
                badge = {
                    if (showBadge) Badge()
                }
            ) {
                if (showBadge)
                    Icon(
                        painterResource(id = resImg),
                        "Move to $resText section",
                        modifier = Modifier.size(dimensionResource(R.dimen.section_buttom_img))
                    )
                else
                    Icon(
                        painterResource(id = resImg),
                        "Move to $resText section",
                        tint = Color.Gray,
                        modifier = Modifier.size(dimensionResource(R.dimen.section_buttom_img))
                    )
            }
        },

//    modifier: Modifier = Modifier,
    label = { if (showBadge) Text (resText)
            else Text(resText, color = Color.Gray)
            },
    alwaysShowLabel = true,
    )
}

/**
 * Composable that displays what the UI of the app looks like in light theme in the design tab.
 */
@Preview(showBackground = true)
@Composable
fun PlantSheetBottomBarPreview() {
    val samplePlant = PlantsDataSource.loadPlants()[0]

    DruidNetTheme {
        Scaffold(
            bottomBar = PlantSheetBottomBar(
                Screen.PlantSheet,
                onClickBottomNavItem = { _ -> {  } },
                currentSection = PlantSheetSection.DESCRIPTION,
                hasConfusions = samplePlant.confusions.isNotEmpty()
            ),
        ) {
            PlantSheetScreen(
                plant = samplePlant,
                currentSection = PlantSheetSection.DESCRIPTION,
                onChangeSection = { { } },
                modifier = Modifier.padding(it)
            )
        }
    }
}
