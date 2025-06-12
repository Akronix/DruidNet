package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import org.druidanet.druidnet.ui.theme.DruidNetTheme

@Composable
fun RecomendationsScreen (
    recommendationsTxt: String,
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(state = scrollState)
    ) {
        Image(
            bitmap = imageBitmap,
            contentScale = ContentScale.FillWidth,
            contentDescription = "Una cesta repleta de diferentes flores silvestres recién recolectadas",
            modifier = Modifier
                .fillMaxWidth()
        )
        SelectionContainer {
            Markdown(
                recommendationsTxt,
                typography = markdownTypography(
                    h1 = MaterialTheme.typography.headlineLarge,
                    h2 = MaterialTheme.typography.headlineMedium,
                    paragraph = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp)
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 30.dp)
            )
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun RecomendationsScreenPreview() {
//    DruidNetTheme(darkTheme = false) {
//        RecomendationsScreen(
//            "**Recomendaciones de recolección**\n" +
//                    "## Recomendación 1\n" +
//                    "Texto de recomendación"
//        )
//    }
//}
