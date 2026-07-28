package org.druidanet.druidnet.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.ui.components.PlantImageCarousel
import org.druidanet.druidnet.ui.theme.DruidNetTheme

/* For expanding the image of the plant to full screen */
@Composable
fun ImageFullScreen(imageUrl : String,
                    attribution : String = "(c) DruidNet CC BY-NC-SA 4.0",
                    modifier: Modifier
                    ) {

    Log.i("ImageFullScreen", "imageUrl: $imageUrl")

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
//                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = stringResource(R.string.datasheet_image_cdescp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .zoomable(rememberZoomableState())
                )
            }
            Spacer(modifier = Modifier.padding(12.dp))
            Text(attribution,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
fun ImageFullScreenPreview() {
    val previewHandler = AsyncImagePreviewHandler {
        ColorImage(Color.LightGray.toArgb())
    }

    DruidNetTheme {
        ImageFullScreen(
            imageUrl = "file:///android_asset/images/plants/satureja.webp",
            attribution = "CC-BY-NC DruidNet",
            modifier = Modifier.fillMaxSize()
        )
    }
}
