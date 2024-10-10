package org.druidanet.druidnetbeta.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import org.druidanet.druidnetbeta.R
import org.druidanet.druidnetbeta.ui.theme.DruidNetBetaTheme

@Composable
fun WelcomeScreen(onCatalogButtonClick: () -> Unit, modifier: Modifier = Modifier) {

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(R.drawable.druid),
                contentDescription = "An image of a druid")
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
                onClick = onCatalogButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("\uD83D\uDCD6 " + stringResource(R.string.greetings_catalog_btn))
            }
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            ) {
                Text("🔮 " + stringResource(R.string.greetings_identifier_btn))
            }
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun WelcomePreviewDark() {
//    DruidNetBetaTheme(darkTheme = true) {
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
    DruidNetBetaTheme(darkTheme = false) {
        WelcomeScreen(
            onCatalogButtonClick = {},
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center))
    }
}