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

// ---------------------------------------------------------------------------
// Syntax highlighting colours
// ---------------------------------------------------------------------------
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
    val lineNumberActive: Color,
    val gutterBg: Color,
    val selectionBg: Color,
    val currentLineBg: Color
)

val LocalSyntaxColors = staticCompositionLocalOf {
    DarkSyntaxColors
}

// ---------------------------------------------------------------------------
// IDE chrome colours (toolbar / tool windows / tabs / tree / status bar)
// ---------------------------------------------------------------------------
data class IdeColors(
    val toolbarBg: Color,
    val panelBg: Color,
    val editorBg: Color,
    val tabActiveBg: Color,
    val tabInactiveBg: Color,
    val tabAccent: Color,
    val treeSelectionBg: Color,
    val statusBarBg: Color,
    val border: Color,
    val mutedText: Color,
    val accent: Color
)

val LocalIdeColors = staticCompositionLocalOf {
    DarkIdeColors
}

// ---------------------------------------------------------------------------
// Material colour schemes
// ---------------------------------------------------------------------------
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
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
    onSurfaceVariant = DarkOnBackground,
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
    onSurfaceVariant = DarkPurpleOnBackground,
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
    onSurfaceVariant = UltraDarkOnBackground,
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
    onSurfaceVariant = DarkRedOnBackground,
    error = DarkRedError,
    outline = DarkRedOutline
)

// ---------------------------------------------------------------------------
// Syntax palettes
// ---------------------------------------------------------------------------
private val LightSyntaxColors = SyntaxColors(
    keyword = Color(0xFF0033B3),
    string = Color(0xFF067D17),
    comment = Color(0xFF8C8C8C),
    number = Color(0xFF1750EB),
    type = Color(0xFF000080),
    function = Color(0xFF00627A),
    operator = Color(0xFF1E1F22),
    annotation = Color(0xFF9E880D),
    tag = Color(0xFF0033B3),
    attribute = Color(0xFF174AD4),
    plain = Color(0xFF1E1F22),
    lineNumber = Color(0xFFADADAD),
    lineNumberActive = Color(0xFF3C3C3C),
    gutterBg = Color(0xFFF7F8FA),
    selectionBg = Color(0xFFB4D7FF),
    currentLineBg = Color(0xFFF0F4FF)
)

internal val DarkSyntaxColors = SyntaxColors(
    keyword = Color(0xFFCF8E6D),
    string = Color(0xFF6AAB73),
    comment = Color(0xFF7A7E85),
    number = Color(0xFF2AACB8),
    type = Color(0xFFB5B6E3),
    function = Color(0xFF56A8F5),
    operator = Color(0xFFDFE1E5),
    annotation = Color(0xFFB3AE60),
    tag = Color(0xFFCF8E6D),
    attribute = Color(0xFF87C3FF),
    plain = Color(0xFFBCBEC4),
    lineNumber = Color(0xFF4B5059),
    lineNumberActive = Color(0xFFA1A3AB),
    gutterBg = Color(0xFF1E1F22),
    selectionBg = Color(0xFF214283),
    currentLineBg = Color(0xFF26282E)
)

private val DarkPurpleSyntaxColors = SyntaxColors(
    keyword = Color(0xFFC792EA),
    string = Color(0xFF98D58A),
    comment = Color(0xFF6E6184),
    number = Color(0xFF7FD0E0),
    type = Color(0xFFE0B0FF),
    function = Color(0xFF82AAFF),
    operator = Color(0xFFF1EAFA),
    annotation = Color(0xFFE6C75C),
    tag = Color(0xFFC792EA),
    attribute = Color(0xFF9FB8FF),
    plain = Color(0xFFE3DAF2),
    lineNumber = Color(0xFF4A3D63),
    lineNumberActive = Color(0xFFC2AEE0),
    gutterBg = Color(0xFF120C1C),
    selectionBg = Color(0xFF42286B),
    currentLineBg = Color(0xFF1E1530)
)

private val UltraDarkSyntaxColors = SyntaxColors(
    keyword = Color(0xFFE0A878),
    string = Color(0xFF7FC585),
    comment = Color(0xFF5A5A5A),
    number = Color(0xFF49C0CC),
    type = Color(0xFFC9C9F0),
    function = Color(0xFF6FB3F7),
    operator = Color(0xFFDADADA),
    annotation = Color(0xFFC7C176),
    tag = Color(0xFFE0A878),
    attribute = Color(0xFF93C7FF),
    plain = Color(0xFFD4D4D4),
    lineNumber = Color(0xFF3A3A3A),
    lineNumberActive = Color(0xFFB0B0B0),
    gutterBg = Color(0xFF000000),
    selectionBg = Color(0xFF1F3A5F),
    currentLineBg = Color(0xFF101012)
)

private val DarkRedSyntaxColors = SyntaxColors(
    keyword = Color(0xFFFF8A66),
    string = Color(0xFF9CCC65),
    comment = Color(0xFF7E5959),
    number = Color(0xFFFFB74D),
    type = Color(0xFFFFB0A8),
    function = Color(0xFFFF8A80),
    operator = Color(0xFFFCE4E4),
    annotation = Color(0xFFE6C75C),
    tag = Color(0xFFFF8A66),
    attribute = Color(0xFFFFAB91),
    plain = Color(0xFFF0D6D6),
    lineNumber = Color(0xFF6B3838),
    lineNumberActive = Color(0xFFE0A8A8),
    gutterBg = Color(0xFF140A0A),
    selectionBg = Color(0xFF5C2424),
    currentLineBg = Color(0xFF20100F)
)

// ---------------------------------------------------------------------------
// IDE chrome palettes
// ---------------------------------------------------------------------------
private val LightIdeColors = IdeColors(
    toolbarBg = Color(0xFFF7F8FA),
    panelBg = Color(0xFFF7F8FA),
    editorBg = Color(0xFFFFFFFF),
    tabActiveBg = Color(0xFFFFFFFF),
    tabInactiveBg = Color(0xFFECEDF1),
    tabAccent = Color(0xFF3574F0),
    treeSelectionBg = Color(0xFFD4E2FF),
    statusBarBg = Color(0xFFF7F8FA),
    border = Color(0xFFD3D5DB),
    mutedText = Color(0xFF818594),
    accent = Color(0xFF3574F0)
)

internal val DarkIdeColors = IdeColors(
    toolbarBg = Color(0xFF2B2D30),
    panelBg = Color(0xFF2B2D30),
    editorBg = Color(0xFF1E1F22),
    tabActiveBg = Color(0xFF1E1F22),
    tabInactiveBg = Color(0xFF2B2D30),
    tabAccent = Color(0xFF548AF7),
    treeSelectionBg = Color(0xFF2E436E),
    statusBarBg = Color(0xFF2B2D30),
    border = Color(0xFF1E1F22),
    mutedText = Color(0xFF868A91),
    accent = Color(0xFF548AF7)
)

private val DarkPurpleIdeColors = IdeColors(
    toolbarBg = Color(0xFF1C1330),
    panelBg = Color(0xFF1A1228),
    editorBg = Color(0xFF120C1C),
    tabActiveBg = Color(0xFF120C1C),
    tabInactiveBg = Color(0xFF1C1330),
    tabAccent = Color(0xFFC792EA),
    treeSelectionBg = Color(0xFF3A2460),
    statusBarBg = Color(0xFF1C1330),
    border = Color(0xFF2A1D44),
    mutedText = Color(0xFF8A7BA8),
    accent = Color(0xFFC792EA)
)

private val UltraDarkIdeColors = IdeColors(
    toolbarBg = Color(0xFF0B0B0D),
    panelBg = Color(0xFF060607),
    editorBg = Color(0xFF000000),
    tabActiveBg = Color(0xFF000000),
    tabInactiveBg = Color(0xFF0B0B0D),
    tabAccent = Color(0xFF8AB4F8),
    treeSelectionBg = Color(0xFF1C2A40),
    statusBarBg = Color(0xFF0B0B0D),
    border = Color(0xFF1A1A1C),
    mutedText = Color(0xFF6E6E73),
    accent = Color(0xFF8AB4F8)
)

private val DarkRedIdeColors = IdeColors(
    toolbarBg = Color(0xFF201111),
    panelBg = Color(0xFF1C0F0F),
    editorBg = Color(0xFF140A0A),
    tabActiveBg = Color(0xFF140A0A),
    tabInactiveBg = Color(0xFF201111),
    tabAccent = Color(0xFFFF5C5C),
    treeSelectionBg = Color(0xFF4A1F1F),
    statusBarBg = Color(0xFF201111),
    border = Color(0xFF321A1A),
    mutedText = Color(0xFFA87B7B),
    accent = Color(0xFFFF5C5C)
)

// ---------------------------------------------------------------------------
// Theme entry point
// ---------------------------------------------------------------------------
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

    val ideColors = when (appTheme) {
        AppTheme.LIGHT -> LightIdeColors
        AppTheme.DARK -> DarkIdeColors
        AppTheme.DARK_PURPLE -> DarkPurpleIdeColors
        AppTheme.ULTRA_DARK -> UltraDarkIdeColors
        AppTheme.DARK_RED -> DarkRedIdeColors
    }

    val animSpec = tween<Color>(durationMillis = 280)
    val animatedColorScheme = targetColorScheme.copy(
        primary = animateColorAsState(targetColorScheme.primary, animSpec, label = "primary").value,
        secondary = animateColorAsState(targetColorScheme.secondary, animSpec, label = "secondary").value,
        background = animateColorAsState(targetColorScheme.background, animSpec, label = "background").value,
        surface = animateColorAsState(targetColorScheme.surface, animSpec, label = "surface").value,
        onPrimary = animateColorAsState(targetColorScheme.onPrimary, animSpec, label = "onPrimary").value,
        onBackground = animateColorAsState(targetColorScheme.onBackground, animSpec, label = "onBackground").value,
        onSurface = animateColorAsState(targetColorScheme.onSurface, animSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(targetColorScheme.surfaceVariant, animSpec, label = "surfaceVariant").value,
        error = animateColorAsState(targetColorScheme.error, animSpec, label = "error").value,
        outline = animateColorAsState(targetColorScheme.outline, animSpec, label = "outline").value
    )

    CompositionLocalProvider(
        LocalSyntaxColors provides syntaxColors,
        LocalIdeColors provides ideColors
    ) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = CodeViewerTypography,
            shapes = CodeViewerShapes,
            content = content
        )
    }
}
