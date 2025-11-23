package ai.data

enum class WeekendMode(val id: String, val displayName: String) {
    FriSat("FriSat", "Friday & Saturday"),
    SatSun("SatSun", "Saturday & Sunday");

    companion object {
        const val KEY = "weekendMode"
        fun fromId(id: String): WeekendMode = values().find { it.id == id } ?: SatSun
    }
}
