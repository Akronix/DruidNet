package org.druidanet.druidnet.ui

import Screen
import android.content.res.Resources.Theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.druidanet.druidnet.AboutDestination
import org.druidanet.druidnet.CatalogDestination
import org.druidanet.druidnet.R
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun WelcomeScreen(onNavigationButtonClick: (Screen) -> Unit, modifier: Modifier = Modifier) {

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
                contentDescription = "An image of a druid and a druidess",)
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

//@Preview(showBackground = true)
//@Composable
//fun WelcomePreviewDark() {
//    DruidNetTheme(darkTheme = true) {
//        WelcomeScreen(
//            onCatalogButtonClick = {},
//            modifier = Modifier
//                .fillMaxSize()
//                .wrapContentSize(Alignment.Center))
//    }
//}

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