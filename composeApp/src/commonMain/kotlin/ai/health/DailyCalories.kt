package ai.health

data class DailyCalories(
    val date: String,
    val caloriesKcal: Double,
    val sourceId: String,
    val fetchedAt: Long
)
