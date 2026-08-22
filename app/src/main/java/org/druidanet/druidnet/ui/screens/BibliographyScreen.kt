package org.druidanet.druidnet.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography

@Composable
fun BibliographyScreen (
    modifier: Modifier = Modifier,
    viewModel: BibliographyViewModel = hiltViewModel()
) {
    val bibliographyStr by viewModel.bibliographyMarkdown.collectAsState()
    
    Box(
        modifier = modifier
            .verticalScroll(state = ScrollState(0))
    )
    {

        SelectionContainer {

            Markdown(
                bibliographyStr.trimIndent(),
                typography = markdownTypography(
                    text = MaterialTheme.typography.bodyMedium,
                    link = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        textDecoration = TextDecoration.Underline
                    )
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 30.dp)
            )
        }
    }

}

