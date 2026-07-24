package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.model.OfflineRussiaRegion
import auto.atom.yandexmapdownloadmanager.protocol.model.FileDataPayload
import java.io.File
import java.io.FileOutputStream

/**
 * Принимает последовательность пакетов с данными файлов региона
 * и восстанавливает исходные файлы на диске.
 */
class RegionFileReceiver(

    /**
     * Корневая директория, в которую сохраняются полученные регионы.
     */
    private val root: File
) {

    /**
     * Поток записи текущего принимаемого файла.
     *
     * Открывается при начале получения нового файла и закрывается
     * после получения последнего пакета этого файла.
     */
    private var output: FileOutputStream? = null

    /**
     * Файл, который в данный момент принимается.
     *
     * Используется для определения момента переключения
     * на следующий файл.
     */
    private var currentFile: File? = null

    /**
     * Обрабатывает очередной пакет данных файла.
     *
     * Если пакет относится к новому файлу, открывается новый поток
     * записи. Полученные байты дописываются в файл.
     *
     * После получения последнего пакета файл закрывается.
     *
     * @param chunk Полученные данные файла.
     * @param lastPart Признак того, что это последний пакет файла.
     */
    fun onChunk(
        chunk: FileDataPayload,
        lastPart: Boolean
    ) {

        val folder = OfflineRussiaRegion.folderName(chunk.regionId)

        // Формируем путь:
        // <root>/<regionId>/<fileName>
        val file = File(
            root,
            "$folder/${chunk.regionId}/${chunk.fileName}"
        )


        // Если начали получать другой файл —
        // закрываем предыдущий и открываем новый.
        if (currentFile?.absolutePath != file.absolutePath) {

            // Закрываем предыдущий поток записи.
            output?.close()

            // Создаем каталог региона, если он отсутствует.
            file.parentFile.mkdirs()

            // Запоминаем текущий файл.
            currentFile = file

            // Открываем поток записи нового файла.
            output = FileOutputStream(file)
        }

        // Дописываем полученные байты в конец файла.
        output!!.write(chunk.bytes)

        // Если это последний пакет файла —
        // гарантируем запись данных на диск и освобождаем ресурсы.
        if (lastPart) {

            // Принудительно записываем все буферы.
            output!!.flush()

            // Закрываем файл.
            output!!.close()

            // Сбрасываем состояние приемника.
            output = null
            currentFile = null
        }
    }
}