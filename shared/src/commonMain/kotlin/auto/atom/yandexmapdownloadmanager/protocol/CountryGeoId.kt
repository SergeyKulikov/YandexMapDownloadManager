package auto.atom.yandexmapdownloadmanager.protocol

enum class CountryGeoID(val id: Int, val localizedName: String) {
    ABKHAZIA(20711, "Абхазия"),
    AZERBAIJAN(191, "Азербайджан"),
    AUSTRALIA(29, "Австралия"),
    AUSTRIA(116, "Австрия"),
    ARMENIA(195, "Армения"),
    BELARUS(149, "Беларусь"),
    BELGIUM(118, "Бельгия"),
    BULGARIA(152, "Болгария"),
    BRAZIL(73, "Бразилия"),
    UNITED_KINGDOM(105, "Великобритания"),
    HUNGARY(146, "Венгрия"),
    VIETNAM(136, "Вьетнам"),
    GERMANY(114, "Германия"),
    GREECE(127, "Греция"),
    GEORGIA(189, "Грузия"),
    DENMARK(112, "Дания"),
    EGYPT(104, "Египет"),
    ISRAEL(132, "Израиль"),
    INDIA(99, "Индия"),
    SPAIN(129, "Испания"),
    ITALY(125, "Италия"),
    KAZAKHSTAN(159, "Казахстан"),
    CANADA(91, "Канада"),
    KYRGYZSTAN(197, "Кыргызстан"),
    CHINA(130, "Китай"),
    LATVIA(205, "Латвия"),
    LITHUANIA(206, "Литва"),
    MOLDOVA(207, "Молдова"),
    NETHERLANDS(121, "Нидерланды"),
    NORWAY(111, "Норвегия"),
    UAE(97, "ОАЭ"),
    POLAND(141, "Польша"),
    RUSSIA(225, "Россия"),
    ROMANIA(151, "Румыния"),
    USA(84, "США"),
    TAJIKISTAN(201, "Таджикистан"),
    THAILAND(137, "Таиланд"),
    TURKMENISTAN(203, "Туркменистан"),
    TURKEY(134, "Турция"),
    UZBEKISTAN(193, "Узбекистан"),
    UKRAINE(187, "Украина"),
    FINLAND(147, "Финляндия"),
    FRANCE(124, "Франция"),
    CZECHIA(143, "Чехия"),
    SWITZERLAND(115, "Швейцария"),
    SWEDEN(109, "Швеция"),
    SOUTH_OSSETIA(20712, "Южная Осетия"),
    SOUTH_KOREA(133, "Южная Корея"),
    ESTONIA(204, "Эстония"),
    JAPAN(131, "Япония");

    companion object {
        // Быстрый поиск страны по её GeoID (например, для парсинга URI из MapKit)
        fun fromId(id: Int): CountryGeoID? {
            return entries.find { it.id == id }
        }

        // Поиск по ID с дефолтным значением, чтобы избежать null-safety проблем
        fun fromIdOrDefault(id: Int, default: CountryGeoID = RUSSIA): CountryGeoID {
            return entries.find { it.id == id } ?: default
        }
    }
}