package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.DEFAULT_CREDITS_TXT

@Composable
fun RecomendationsScreen (
    recommendationsTxt: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier.verticalScroll(state = scrollState)
    ) {
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

@Preview(showBackground = true)
@Composable
fun RecomendationsScreenPreview() {
    DruidNetTheme(darkTheme = false) {
        RecomendationsScreen(
            "# ¿Recomendaciones de recolección?\n" +
                    "## Recomendación 1\n" +
                    "Texto de recomendación"
        )
    }
}
