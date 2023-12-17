package com.brandonhxrr.vault.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.brandonhxrr.vault.R

val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.product_sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.product_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.product_sans_bold)),
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.product_sans_bold)),
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
)