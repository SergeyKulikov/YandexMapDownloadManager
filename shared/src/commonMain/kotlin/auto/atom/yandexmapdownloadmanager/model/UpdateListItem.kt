package auto.atom.yandexmapdownloadmanager.model

sealed interface UpdateListItem {

    data class Header(
        val letter: Char
    ) : UpdateListItem

    data class Region(
        val task: RegionUpdateTask
    ) : UpdateListItem
}