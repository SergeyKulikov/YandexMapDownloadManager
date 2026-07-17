package auto.atom.yandexmapdownloadmanager.protocol.map

/**
 * Автоматически сформировано по getRegions.json (Offline MapKit).
 */
enum class OfflineRussiaRegion(
    val id: Int,
    val regionName: String
) {
    MOSCOW_AND_MOSCOW_OBLAST(1, "Москва и Московская область"),
    MOSCOW(213, "Москва"),

    SAINT_PETERSBURG_AND_LENINGRAD_OBLAST(10174, "Санкт-Петербург и Ленинградская область"),
    SAINT_PETERSBURG(2, "Санкт-Петербург"),

    NENETS_AUTONOMOUS_OKRUG(10176, "Ненецкий автономный округ"),

    REPUBLIC_OF_ALTAI(10231, "Республика Алтай"),
    REPUBLIC_OF_TYVA(10233, "Республика Тыва"),
    JEWISH_AUTONOMOUS_OBLAST(10243, "Еврейская автономная область"),
    CHUKOTKA_AUTONOMOUS_OKRUG(10251, "Чукотский автономный округ"),

    BELGOROD_OBLAST(10645, "Белгородская область"),
    BRYANSK_OBLAST(10650, "Брянская область"),
    VLADIMIR_OBLAST(10658, "Владимирская область"),
    VORONEZH_OBLAST(10672, "Воронежская область"),
    IVANOVO_OBLAST(10687, "Ивановская область"),
    KALUGA_OBLAST(10693, "Калужская область"),
    KOSTROMA_OBLAST(10699, "Костромская область"),
    KURSK_OBLAST(10705, "Курская область"),
    LIPETSK_OBLAST(10712, "Липецкая область"),
    ORYOL_OBLAST(10772, "Орловская область"),
    RYAZAN_OBLAST(10776, "Рязанская область"),
    SMOLENSK_OBLAST(10795, "Смоленская область"),

    TAMBOV_OBLAST(10802, "Тамбовская область"),
    TVER_OBLAST(10819, "Тверская область"),
    YAROSLAVL_OBLAST(10841, "Ярославская область"),
    ARKHANGELSK_OBLAST(10842, "Архангельская область"),
    VOLOGDA_OBLAST(10853, "Вологодская область"),
    MURMANSK_OBLAST(10897, "Мурманская область"),

    NOVGOROD_OBLAST(10904, "Новгородская область"),
    PSKOV_OBLAST(10926, "Псковская область"),
    REPUBLIC_OF_KARELIA(10933, "Республика Карелия"),
    REPUBLIC_OF_KOMI(10939, "Республика Коми"),
    ASTRAKHAN_OBLAST(10946, "Астраханская область"),
    VOLGOGRAD_OBLAST(10950, "Волгоградская область"),

    KRASNODAR_KRAI(10995, "Краснодарский край"),

    REPUBLIC_OF_ADYGEA(11004, "Республика Адыгея"),
    REPUBLIC_OF_DAGESTAN(11010, "Республика Дагестан"),
    REPUBLIC_OF_INGUSHETIA(11012, "Республика Ингушетия"),
    KABARDINO_BALKARIAN_REPUBLIC(11013, "Кабардино-Балкарская Республика"),
    REPUBLIC_OF_KALMYKIA(11015, "Республика Калмыкия"),
    KARACHAY_CHERKESS_REPUBLIC(11020, "Карачаево-Черкесская Республика"),
    REPUBLIC_OF_NORTH_OSSETIA_ALANIA(
        11021,
        "Республика Северная Осетия — Алания"
    ),
    CHECHEN_REPUBLIC(11024, "Чеченская Республика"),

    ROSTOV_OBLAST(11029, "Ростовская область"),

    STAVROPOL_KRAI(11069, "Ставропольский край"),

    KIROV_OBLAST(11070, "Кировская область"),

    REPUBLIC_OF_MARI_EL(11077, "Республика Марий Эл"),

    NIZHNY_NOVGOROD_OBLAST(11079, "Нижегородская область"),

    ORENBURG_OBLAST(11084, "Оренбургская область"),

    PENZA_OBLAST(11095, "Пензенская область"),

    PERM_KRAI(11108, "Пермский край"),

    REPUBLIC_OF_BASHKORTOSTAN(11111, "Республика Башкортостан"),
    REPUBLIC_OF_MORDOVIA(11117, "Республика Мордовия"),
    REPUBLIC_OF_TATARSTAN(11119, "Республика Татарстан"),

    SAMARA_OBLAST(11131, "Самарская область"),
    SARATOV_OBLAST(11146, "Саратовская область"),

    UDMURT_REPUBLIC(11148, "Удмуртская Республика"),

    ULYANOVSK_OBLAST(11153, "Ульяновская область"),

    CHUVASH_REPUBLIC(11156, "Чувашская Республика"),

    KURGAN_OBLAST(11158, "Курганская область"),

    SVERDLOVSK_OBLAST(11162, "Свердловская область"),
    TYUMEN_OBLAST(11176, "Тюменская область"),

    KHANTY_MANSI_AUTONOMOUS_OKRUG_YUGRA(
        11193,
        "Ханты-Мансийский автономный округ — Югра"
    ),

    CHELYABINSK_OBLAST(11225, "Челябинская область"),

    YAMALO_NENETS_AUTONOMOUS_OKRUG(
        11232,
        "Ямало-Ненецкий автономный округ"
    ),

    ALTAI_KRAI(11235, "Алтайский край"),

    IRKUTSK_OBLAST(11266, "Иркутская область"),

    KEMEROVO_OBLAST_KUZBASS(
        11282,
        "Кемеровская область (Кузбасс)"
    ),

    KRASNOYARSK_KRAI(11309, "Красноярский край"),
    NOVOSIBIRSK_OBLAST(11316, "Новосибирская область"),
    OMSK_OBLAST(11318, "Омская область"),
    REPUBLIC_OF_BURYATIA(11330, "Республика Бурятия"),
    REPUBLIC_OF_KHAKASSIA(11340, "Республика Хакасия"),
    TOMSK_OBLAST(11353, "Томская область"),
    AMUR_OBLAST(11375, "Амурская область"),
    KAMCHATKA_KRAI(11398, "Камчатский край"),
    MAGADAN_OBLAST(11403, "Магаданская область"),
    PRIMORSKY_KRAI(11409, "Приморский край"),
    REPUBLIC_OF_SAKHA_YAKUTIA(11443, "Республика Саха (Якутия)"),

    SAKHALIN_OBLAST(11450, "Сахалинская область"),

    KHABAROVSK_KRAI(11457, "Хабаровский край"),

    ZABAIKALSKY_KRAI(21949, "Забайкальский край"),

    REPUBLIC_OF_CRIMEA(977, "Республика Крым");

    companion object {
        fun fromId(id: Int): OfflineRussiaRegion? =
            entries.find { it.id == id }
    }
}