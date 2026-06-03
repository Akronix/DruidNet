package org.druidanet.druidnet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.druidanet.druidnet.R
import org.druidanet.druidnet.ui.theme.DruidNetTheme
import org.druidanet.druidnet.utils.forwardingPainter
import kotlin.math.absoluteValue

@Composable
fun PlantImageCarousel(
    imageUrls: List<String>,
    plantName: String = "",
    modifier: Modifier = Modifier
) {
    // 1. Initialize the state with the number of pages
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // 2. The Swipeable Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),

            // Optional padding to show a preview of the next/previous image
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 8.dp
        ) { page ->
            val url = imageUrls[page]

            // Swap thumbnail for large size if pulling from iNaturalist
//            val highResUrl = url.replace("square.jpg", "large.jpg")

//            Card(
//                shape = RoundedCornerShape(16.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                modifier = Modifier.fillMaxSize()
//            ) {
//                AsyncImage(
//                    model = highResUrl,
//                    contentDescription = "Plant image $page",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
//            Card(
//                modifier = Modifier
//                    .graphicsLayer {
//                    // Calculate the absolute offset for the current page from the
//                    // scroll position. We use the absolute value which allows us to mirror
//                    // any effects for both directions
//                    val pageOffset = (
//                            (pagerState.currentPage - page) + pagerState
//                                .currentPageOffsetFraction
//                            ).absoluteValue
//
//                    // We animate the alpha, between 50% and 100%
//                    alpha = lerp(
//                        start = 0.5f,
//                        stop = 1f,
//                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                    )
//                }
//            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "PlantNet image for $plantName",
                    modifier = Modifier
                        .fillMaxWidth()
                        .zoomable(rememberZoomableState())
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                    ,
                    contentScale = ContentScale.FillWidth,
                    fallback = painterResource(R.drawable.grass),
                    placeholder = forwardingPainter(
                        painter = painterResource(R.drawable.eco),
                        colorFilter = ColorFilter.tint(Color.Gray),
                        alpha = 0.5f,
                    )
                )
//            }
        }

        // 3. Carousel Indicators (The dots at the bottom)
        if (imageUrls.size > 1) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .wrapContentHeight(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
            ) {
                items(imageUrls.size) { index ->
                    // Change color dynamically depending on if this dot is the active page
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .background(
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
fun PlantImageCarouselPreview() {

    val sampleImages = listOf(
        "file://home/akronix/workspace/DruidNet/app/src/main/assets/images/plants/achillea_millefolium.webp",
        "https://inaturalist-open-data.s3.amazonaws.com/photos/672062604/square.jpg",
    )
    DruidNetTheme(darkTheme = false) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.height(300.dp)) {
                PlantImageCarousel(imageUrls = sampleImages)
            }
            Spacer(Modifier.fillMaxSize())
        }
    }
}
