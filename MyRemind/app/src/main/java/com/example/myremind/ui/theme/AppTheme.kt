
package com.example.myremind.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val BlackBg = Color(0xFF0A0A0F)       
val CardBg = Color(0xFF2D2C40)        
val TextPrimary = Color(0xFFFFFFFF)   
val TextSecondary = Color(0xFFB7B7C8) 
val AccentYellow = Color(0xFFF5F54A)  
val DangerRed = Color(0xFFFF2B2B)     

private val DarkColorScheme = darkColorScheme(
    primary = AccentYellow,
    onPrimary = Color.Black,
    secondary = CardBg,
    background = BlackBg,
    surface = CardBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

val AppTypography = Typography(
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = TextSecondary
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
    )
)

@Composable
fun MyRemindTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        shapes = AppShapes,
        typography = AppTypography,
        content = content
    )
}