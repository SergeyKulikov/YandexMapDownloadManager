package auto.atom.yandexmapdownloadmanager

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.app_icon
import auto.atom.yandexmapdownloadmanager.ui.App
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension

fun main() = application {

    val viewModel = MainViewModel()

    Runtime.getRuntime().addShutdownHook(
        Thread {
            viewModel.shutdown()
        }
    )

    Window(
        onCloseRequest = {
            viewModel.shutdown()
            exitApplication()
        },
        title = "Atom Yandex Map Manager",
        icon = painterResource(Res.drawable.app_icon),
        state = WindowState(
            width = 1600.dp,
            height = 900.dp,
            position = WindowPosition.Aligned(
                alignment = androidx.compose.ui.Alignment.Center
            )
        )
    ) {
        window.minimumSize = Dimension(1024, 768)

        App(viewModel)
    }
}