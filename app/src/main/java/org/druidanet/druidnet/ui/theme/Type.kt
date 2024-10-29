package org.druidanet.druidnet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.sp
import org.druidanet.druidnet.R

//// Set of Material typography styles to start with
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)

val bodyFontFamily = FontFamily(
    Font(R.font.outfit_regular),
    Font(R.font.outfit_light, FontWeight.Light),
    Font(R.font.outfit_bold, FontWeight.Bold),
    Font(R.font.outfit_medium, FontWeight.Medium)
)
val labelFontFamily = FontFamily(
    Font(R.font.ubuntu_regular),
    Font(R.font.ubuntu_bold, FontWeight.Bold),
    Font(R.font.ubuntu_medium, FontWeight.Medium)
)

val displayFontFamily = FontFamily(
    Font(R.font.cherryswash_regular),
    Font(R.font.cherryswash_bold, FontWeight.Bold)
)

val titleFontFamily = bodyFontFamily

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = titleFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = titleFontFamily, fontSize = 20.sp),
    titleSmall = baseline.titleSmall.copy(fontFamily = titleFontFamily, fontSize = 16.sp),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily, lineBreak = LineBreak.Paragraph),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily, lineBreak = LineBreak.Paragraph, fontSize = 15.sp),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily, lineBreak = LineBreak.Paragraph, fontSize = 13.sp),
    labelLarge = baseline.labelLarge.copy(fontFamily = labelFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = labelFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = labelFontFamily),
)