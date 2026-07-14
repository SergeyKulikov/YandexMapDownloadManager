package auto.atom.yandexmapdownloadmanager.setting


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

/**
 * Менеджер хранения настроек в JSON.
 *
 * Ограничения:
 * - T должен быть обычным классом (не коллекцией и не generic-типом).
 * - Имя файла автоматически формируется как "<SimpleClassName>.ini".
 *
 * Например:
 * TileServiceSettings -> TileServiceSettings.ini
 */
class JsonSettingsManager<T>(
    /**
     * Каталог, в котором будут храниться настройки.
     */
    private val settingsDirectory: File,

    private val type: Type,

    private val defaultValue: T,
) {

    private val gson = Gson()

    private val settingsFileName =
        "${type.typeName.substringAfterLast('.')}.ini"

    private fun getSettingsFile(): File =
        File(settingsDirectory, settingsFileName)

    /**
     * Загружает настройки.
     *
     * Если файл отсутствует или поврежден,
     * создается файл с настройками по умолчанию.
     */
    fun loadSettings(): T {

        val file = getSettingsFile()

        if (!file.exists()) {
            saveSettings(defaultValue)
            return defaultValue
        }

        return try {

            gson.fromJson<T>(
                file.readText(),
                type
            ) ?: defaultValue

        } catch (ex: Exception) {

            ex.printStackTrace()

            saveSettings(defaultValue)

            defaultValue
        }
    }

    /**
     * Сохраняет настройки.
     *
     * @return true при успешном сохранении.
     */
    fun saveSettings(settings: T): Boolean {

        return try {

            settingsDirectory.mkdirs()

            val file = getSettingsFile()

            if (!file.exists()) {
                file.createNewFile()
            }

            file.writeText(
                gson.toJson(settings)
            )

            true

        } catch (ex: Exception) {

            ex.printStackTrace()

            false
        }
    }

    companion object {

        /**
         * Создает менеджер настроек.
         */
        inline fun <reified T> create(
            settingsDirectory: File,
            defaultValue: T
        ): JsonSettingsManager<T> {

            return JsonSettingsManager(
                settingsDirectory = settingsDirectory,
                type = object : TypeToken<T>() {}.type,
                defaultValue = defaultValue
            )
        }
    }
}