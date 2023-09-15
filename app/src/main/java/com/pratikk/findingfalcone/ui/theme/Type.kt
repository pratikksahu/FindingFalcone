package com.pratikk.findingfalcone.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.pratikk.findingfalcone.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val fonts = listOf("Roboto","Open Sans","Lato","Inter")
val fontName = GoogleFont(fonts[1])
val fontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)
fun Typography.defaultFontFamily(fontFamily: FontFamily): Typography {
    return this.copy(
        displayLarge = this.displayLarge.merge(fontFamily = fontFamily),
        displayMedium = this.displayMedium.merge(fontFamily = fontFamily),
        displaySmall = this.displaySmall.merge(fontFamily = fontFamily),
        headlineLarge = this.headlineLarge.merge(fontFamily = fontFamily),
        headlineMedium = this.headlineMedium.merge(fontFamily = fontFamily),
        headlineSmall = this.headlineSmall.merge(fontFamily = fontFamily),
        titleLarge = this.titleLarge.merge(fontFamily = fontFamily),
        titleMedium = this.titleMedium.merge(fontFamily = fontFamily),
        titleSmall = this.titleSmall.merge(fontFamily = fontFamily),
        bodyLarge = this.bodyLarge.merge(fontFamily = fontFamily),
        bodyMedium = this.bodyMedium.merge(fontFamily = fontFamily),
        bodySmall = this.bodySmall.merge(fontFamily = fontFamily),
        labelLarge = this.labelLarge.merge(fontFamily = fontFamily),
        labelMedium = this.labelMedium.merge(fontFamily = fontFamily),
        labelSmall = this.labelSmall.merge(fontFamily = fontFamily)
    )
}