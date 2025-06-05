package org.druidanet.druidnet.ui.screens

import NavigationDestination
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.mikepenz.markdown.m3.Markdown
import org.druidanet.druidnet.AboutDestination
import org.druidanet.druidnet.CatalogDestination
import org.druidanet.druidnet.R
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun WelcomeScreen(onNavigationButtonClick: (NavigationDestination) -> Unit,
                  modifier: Modifier = Modifier) {

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
            .padding(top = 40.dp)
            .offset(x = (-40).dp) // Offset from the end by a specific amount
    ) {
        Icon(
            Icons.Outlined.Settings,
            "Abre ajustes de la aplicación",
            modifier = Modifier.clickable { onNavigationButtonClick(AboutDestination) }
            )
    }

    Box(modifier = modifier) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(R.drawable.druids),
                contentDescription = "An image of a druid and a druidess",
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.welcome),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {onNavigationButtonClick(CatalogDestination)},
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("\uD83D\uDCD6 " + stringResource(R.string.greetings_catalog_btn),
                    style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(48.dp))

            Markdown("[Go to Product (via URI)](plant_sheet/Sambucus nigra3)")

//            Text("Próximamente:",
//                color = Color.DarkGray)
//            Button(
//                onClick = {},
//                modifier = Modifier.fillMaxWidth(),
//                enabled = false
//            ) {
//                Text("🔮 " + stringResource(R.string.greetings_identifier_btn))
//            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    DruidNetTheme(darkTheme = false) {
        WelcomeScreen(
            onNavigationButtonClick = { },
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center))
    }
}