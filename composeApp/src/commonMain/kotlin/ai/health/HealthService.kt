package ai.health

interface HealthService {
    suspend fun getCalories(date: String): Result<DailyCalories>
}

expect class HealthServiceFactory {
    fun create(): HealthService
}
