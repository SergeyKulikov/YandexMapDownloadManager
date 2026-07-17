package auto.atom.yandexmapdownloadmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import auto.atom.yandexmapdownloadmanager.ui.App
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel

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
        title = "AtomYandexMapManager",
    ) {
        App(viewModel)
    }
}