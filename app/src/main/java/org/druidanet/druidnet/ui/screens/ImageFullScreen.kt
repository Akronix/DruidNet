package org.druidanet.druidnet.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.SubcomposeAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.ui.components.PlantImageCarousel
import org.druidanet.druidnet.ui.theme.DruidNetTheme

/* For expanding the image of the plant to full screen */
@Composable
fun ImageFullScreen(
    viewModel: ImageFullScreenViewModel,
    modifier: Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val pagerState = rememberPagerState(
            initialPage = uiState.initialIndex
        ) { uiState.images.size }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
        ) {
            PlantImageCarousel(
                imageURIsOrBitMap = uiState.images,
                pagerState = pagerState,
                pagerModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.padding(12.dp))
            val currentAttribution = if (pagerState.currentPage < uiState.attributions.size) {
                uiState.attributions[pagerState.currentPage]
            } else {
                ""
            }
            Text(
                currentAttribution,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
fun ImageFullScreenPreview() {
    val previewHandler = AsyncImagePreviewHandler {
        ColorImage(Color.LightGray.toArgb())
    }

    CompositionLocalProvider(coil3.compose.LocalAsyncImagePreviewHandler provides previewHandler) {
        DruidNetTheme {
            // We can't easily preview the one with ViewModel without a lot of ceremony, 
            // but we can at least show the structure or use a mock if we had one.
            // For now, let's just use the carousel directly for the preview to see it.
            Column(modifier = Modifier.fillMaxSize()) {
                PlantImageCarousel(
                    imageURIsOrBitMap = listOf("file:///android_asset/images/plants/satureja.webp"),
                    modifier = Modifier.weight(1f),
                    pagerModifier = Modifier.fillMaxSize(),
                )
                Text(
                    "CC-BY-NC DruidNet",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp).align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
