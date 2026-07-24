import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.okio)

    implementation(libs.compose.components.resources)

    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
}

compose.desktop {
    application {
        mainClass = "auto.atom.yandexmapdownloadmanager.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Msi, // Windows Installer (.msi)
                TargetFormat.Dmg, // macOS Disk Image (.dmg)
                TargetFormat.Deb  // Linux Debian/Ubuntu (.deb)
            )

            packageName = "Yandex Map Download Manager"
            packageVersion = "1.0.0"
            description = "Application for downloading offline Yandex maps."
            copyright = "\u00A9\u0020\u0032\u0030\u0032\u0036\u0020\u004A\u006F\u0069\u006E\u0074\u0020\u0053\u0074\u006F\u0063\u006B\u0020\u0043\u006F\u006D\u0070\u0061\u006E\u0079\u0020\u0022\u004B\u0041\u004D\u0041\u0022\u002E\u0020\u0043\u0072\u0065\u0061\u0074\u0065\u0064\u0020\u0062\u0079\u0020\u0053\u0065\u0072\u0067\u0065\u0079\u0020\u004B\u0075\u006C\u0069\u006B\u006F\u0076\u002E"

            windows {
                // Настройки только для Windows
                iconFile.set(project.file("../shared/src/commonMain/composeResources/drawable/app_icon_wnd.ico"))
                menu = true
                shortcut = true
            }

            linux {
                // Настройки только для Linux
                iconFile.set(project.file("../shared/src/commonMain/composeResources/drawable/app_icon.png"))
            }

            macOS {
                // Настройки только для macOS
                iconFile.set(project.file("../shared/src/commonMain/composeResources/drawable/app_icon.icns"))
            }
        }
    }
}
