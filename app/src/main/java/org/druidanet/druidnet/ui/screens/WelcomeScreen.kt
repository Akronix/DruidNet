package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.druidanet.druidnet.R
import org.druidanet.druidnet.navigation.AboutDestination
import org.druidanet.druidnet.navigation.CameraDestination
import org.druidanet.druidnet.navigation.CatalogDestination
import org.druidanet.druidnet.navigation.GlossaryDestination
import org.druidanet.druidnet.navigation.NavigationDestination
import org.druidanet.druidnet.navigation.RecommendationsDestination
import org.druidanet.druidnet.navigation.SearchDestination
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun WelcomeScreen(onNavigationButtonClick: (NavigationDestination) -> Unit,
                   modifier: Modifier) {
    Box(
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .padding(top = 40.dp)
                .offset(x = (-40).dp) // Offset from the end by a specific amount
        ) {
            Icon(
                painterResource(R.drawable.settings),
                "Abre ajustes de la aplicación",
                modifier = Modifier.clickable { onNavigationButtonClick(AboutDestination) }
            )
        }

        Box(modifier = modifier) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
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

                // --- MODIFICATION START ---
            Box(
                contentAlignment = Alignment.TopEnd, // Align ribbon to the top end
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onNavigationButtonClick(CameraDestination) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "\uD83D\uDCF7 ",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                        Text(
                            stringResource(R.string.greetings_identifier_btn),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                // The "BETA" ribbon
                Surface(
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .graphicsLayer(
                            rotationZ = 15f // Slightly rotate the ribbon
                        )
                        .offset(x = 10.dp, y = (-8).dp) // Adjust position
                ) {
                    Text(
                        text = "BETA",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

                /* Button without 'BETA' Ribbon
                Button(
                    onClick = {onNavigationButtonClick(CameraDestination)},
//                    onClick = {onNavigationButtonClick(IdentifyDestination)},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("\uD83D\uDCF7 ",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom=3.dp)
                    )
                    Text(
                        stringResource(R.string.greetings_identifier_btn),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                 */
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onNavigationButtonClick(CatalogDestination) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        "\uD83D\uDCD6 " + stringResource(R.string.greetings_catalog_btn),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onNavigationButtonClick(SearchDestination) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = org.druidanet.druidnet.ui.theme.purpleContainerLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        "\uD83D\uDD0D " + stringResource(R.string.greetings_search_btn),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom link buttons

                TextButton(
                    onClick = { onNavigationButtonClick(RecommendationsDestination) },
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Text(
                        "\uD83E\uDDFA " + stringResource(R.string.greetings_recommendations_btn),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.secondary, // Link color
                        ),
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center, // Center the text,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = { onNavigationButtonClick(GlossaryDestination) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "\uD83D\uDD24 " + stringResource(R.string.greetings_glosarry_btn),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.secondary, // Link color
                        ),
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center, // Center the text,)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

            }
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