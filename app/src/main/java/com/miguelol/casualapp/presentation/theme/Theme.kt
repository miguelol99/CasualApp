package com.miguelol.casualapp.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CustomColorScheme = lightColorScheme(
    primary = Gamboge,
    onPrimary = RaisinBlack,
    primaryContainer = Gamboge,
    onPrimaryContainer = Color.White,
    // inversePrimary = Color.Yellow,
    //  secondary = ,
    //  onSecondary = ,
    secondaryContainer = IndigoDye, //active indicator navbar
    onSecondaryContainer = Color.White,
    //  tertiary = ,
    //  onTertiary = ,
    //  tertiaryContainer = ,
    //  onTertiaryContainer = ,
    background = RaisinBlack,
    onBackground = Color.White,
    surface = PrussianBlue,
    onSurface = Color.White,
    surfaceVariant = IndigoDye, //edit text container
    onSurfaceVariant = Color.White, //label text navbar
    //  surfaceTint = ,
    //  inverseSurface = ,
    //  inverseOnSurface = ,
    error = Error,
    //  onError = ,
    //  errorContainer = ,
    //  onErrorContainer = ,
    //  outline = ,
    outlineVariant = PowderBlue,
    //  scrim: Color
)

@Composable
fun CasualAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = CustomColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}