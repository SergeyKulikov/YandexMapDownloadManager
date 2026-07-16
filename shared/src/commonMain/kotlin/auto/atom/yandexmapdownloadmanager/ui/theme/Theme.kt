package auto.atom.yandexmapdownloadmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(

    primary = BluePrimary,
    secondary = BluePrimary,

    surface = Card,
    background = Color.White,

    error = Error,

    outline = Divider
)

private val DarkColors = darkColorScheme(

    primary = BluePrimary,
    secondary = BluePrimary,

    surface = Color(0xFF1F2125),
    background = Color(0xFF16181B),

    error = Error,

    outline = Color(0xFF3E4653)
)

@Composable
fun YandexMapManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme =
            if (darkTheme)
                DarkColors
            else
                LightColors,
        content = content
    )
}