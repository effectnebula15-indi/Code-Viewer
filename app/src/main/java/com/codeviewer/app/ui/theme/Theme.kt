package com.codeviewer.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    DARK_PURPLE("Dark Purple"),
    ULTRA_DARK("Ultra Dark"),
    DARK_RED("Dark Red")
}

data class SyntaxColors(
    val keyword: Color,
    val string: Color,
    val comment: Color,
    val number: Color,
    val type: Color,
    val function: Color,
    val operator: Color,
    val annotation: Color,
    val tag: Color,
    val attribute: Color,
    val plain: Color,
    val lineNumber: Color,
    val gutterBg: Color,
    val selectionBg: Color
)

val LocalSyntaxColors = staticCompositionLocalOf {
    SyntaxColors(
        keyword = Color(0xFFCC7832),
        string = Color(0xFF6A8759),
        comment = Color(0xFF808080),
        number = Color(0xFF6897BB),
        type = Color(0xFFA9B7C6),
        function = Color(0xFFFFC66D),
        operator = Color(0xFFA9B7C6),
        annotation = Color(0xFFBBB529),
        tag = Color(0xFFE8BF6A),
        attribute = Color(0xFFBABABA),
        plain = Color(0xFFA9B7C6),
        lineNumber = Color(0xFF606366),
        gutterBg = Color(0xFF313335),
        selectionBg = Color(0xFF214283)
    )
}

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    error = LightError,
    outline = LightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    error = DarkError,
    outline = DarkOutline
)

private val DarkPurpleColorScheme = darkColorScheme(
    primary = DarkPurplePrimary,
    secondary = DarkPurpleSecondary,
    background = DarkPurpleBackground,
    surface = DarkPurpleSurface,
    onPrimary = DarkPurpleOnPrimary,
    onBackground = DarkPurpleOnBackground,
    onSurface = DarkPurpleOnSurface,
    surfaceVariant = DarkPurpleSurfaceVariant,
    error = DarkPurpleError,
    outline = DarkPurpleOutline
)

private val UltraDarkColorScheme = darkColorScheme(
    primary = UltraDarkPrimary,
    secondary = UltraDarkSecondary,
    background = UltraDarkBackground,
    surface = UltraDarkSurface,
    onPrimary = UltraDarkOnPrimary,
    onBackground = UltraDarkOnBackground,
    onSurface = UltraDarkOnSurface,
    surfaceVariant = UltraDarkSurfaceVariant,
    error = UltraDarkError,
    outline = UltraDarkOutline
)

private val DarkRedColorScheme = darkColorScheme(
    primary = DarkRedPrimary,
    secondary = DarkRedSecondary,
    background = DarkRedBackground,
    surface = DarkRedSurface,
    onPrimary = DarkRedOnPrimary,
    onBackground = DarkRedOnBackground,
    onSurface = DarkRedOnSurface,
    surfaceVariant = DarkRedSurfaceVariant,
    error = DarkRedError,
    outline = DarkRedOutline
)

private val LightSyntaxColors = SyntaxColors(
    keyword = Color(0xFF0033B3),
    string = Color(0xFF067D17),
    comment = Color(0xFF8C8C8C),
    number = Color(0xFF1750EB),
    type = Color(0xFF000000),
    function = Color(0xFF00627A),
    operator = Color(0xFF000000),
    annotation = Color(0xFF9E880D),
    tag = Color(0xFF000080),
    attribute = Color(0xFF0000FF),
    plain = Color(0xFF000000),
    lineNumber = Color(0xFF999999),
    gutterBg = Color(0xFFF0F0F0),
    selectionBg = Color(0xFFA6D2FF)
)

private val DarkSyntaxColors = SyntaxColors(
    keyword = Color(0xFFCC7832),
    string = Color(0xFF6A8759),
    comment = Color(0xFF808080),
    number = Color(0xFF6897BB),
    type = Color(0xFFA9B7C6),
    function = Color(0xFFFFC66D),
    operator = Color(0xFFA9B7C6),
    annotation = Color(0xFFBBB529),
    tag = Color(0xFFE8BF6A),
    attribute = Color(0xFFBABABA),
    plain = Color(0xFFA9B7C6),
    lineNumber = Color(0xFF606366),
    gutterBg = Color(0xFF313335),
    selectionBg = Color(0xFF214283)
)

private val DarkPurpleSyntaxColors = SyntaxColors(
    keyword = Color(0xFFCF8EF4),
    string = Color(0xFF7EC876),
    comment = Color(0xFF7A6E8A),
    number = Color(0xFF80BFFF),
    type = Color(0xFFE8DEF8),
    function = Color(0xFFFFD54F),
    operator = Color(0xFFE8DEF8),
    annotation = Color(0xFFD4A629),
    tag = Color(0xFFECBF6A),
    attribute = Color(0xFFC8B8DC),
    plain = Color(0xFFE8DEF8),
    lineNumber = Color(0xFF5C4F6E),
    gutterBg = Color(0xFF1E1430),
    selectionBg = Color(0xFF3D2660)
)

private val UltraDarkSyntaxColors = SyntaxColors(
    keyword = Color(0xFFAAAAAA),
    string = Color(0xFF88AA88),
    comment = Color(0xFF555555),
    number = Color(0xFF8899AA),
    type = Color(0xFFCCCCCC),
    function = Color(0xFFBBBBBB),
    operator = Color(0xFFCCCCCC),
    annotation = Color(0xFF999999),
    tag = Color(0xFFAAAAAA),
    attribute = Color(0xFF888888),
    plain = Color(0xFFE0E0E0),
    lineNumber = Color(0xFF444444),
    gutterBg = Color(0xFF050505),
    selectionBg = Color(0xFF222222)
)

private val DarkRedSyntaxColors = SyntaxColors(
    keyword = Color(0xFFFF7B7B),
    string = Color(0xFF8BC34A),
    comment = Color(0xFF7A5A5A),
    number = Color(0xFFFFAB40),
    type = Color(0xFFFFCDD2),
    function = Color(0xFFFFD54F),
    operator = Color(0xFFFFCDD2),
    annotation = Color(0xFFFF8A65),
    tag = Color(0xFFFF6B6B),
    attribute = Color(0xFFCE9178),
    plain = Color(0xFFFFCDD2),
    lineNumber = Color(0xFF5A3A3A),
    gutterBg = Color(0xFF1A0E0E),
    selectionBg = Color(0xFF4A1A1A)
)

@Composable
fun CodeViewerTheme(
    appTheme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val targetColorScheme = when (appTheme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.DARK_PURPLE -> DarkPurpleColorScheme
        AppTheme.ULTRA_DARK -> UltraDarkColorScheme
        AppTheme.DARK_RED -> DarkRedColorScheme
    }

    val syntaxColors = when (appTheme) {
        AppTheme.LIGHT -> LightSyntaxColors
        AppTheme.DARK -> DarkSyntaxColors
        AppTheme.DARK_PURPLE -> DarkPurpleSyntaxColors
        AppTheme.ULTRA_DARK -> UltraDarkSyntaxColors
        AppTheme.DARK_RED -> DarkRedSyntaxColors
    }

    val animSpec = tween<Color>(durationMillis = 300)
    val animatedColorScheme = targetColorScheme.copy(
        primary = animateColorAsState(targetColorScheme.primary, animSpec).value,
        secondary = animateColorAsState(targetColorScheme.secondary, animSpec).value,
        background = animateColorAsState(targetColorScheme.background, animSpec).value,
        surface = animateColorAsState(targetColorScheme.surface, animSpec).value,
        onPrimary = animateColorAsState(targetColorScheme.onPrimary, animSpec).value,
        onBackground = animateColorAsState(targetColorScheme.onBackground, animSpec).value,
        onSurface = animateColorAsState(targetColorScheme.onSurface, animSpec).value,
        surfaceVariant = animateColorAsState(targetColorScheme.surfaceVariant, animSpec).value,
        error = animateColorAsState(targetColorScheme.error, animSpec).value,
        outline = animateColorAsState(targetColorScheme.outline, animSpec).value
    )

    CompositionLocalProvider(LocalSyntaxColors provides syntaxColors) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = CodeViewerTypography,
            shapes = CodeViewerShapes,
            content = content
        )
    }
}
