package ai.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.Clock

class AndroidHealthService(private val context: Context) : HealthService {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    override suspend fun getCalories(date: String): Result<DailyCalories> {
        return try {
            val localDate = LocalDate.parse(date)
            val timeZone = TimeZone.currentSystemDefault()
            val startOfDay = localDate.atStartOfDayIn(timeZone)
            val endOfDay = localDate.plus(1, kotlinx.datetime.DateTimeUnit.DAY).atStartOfDayIn(timeZone)

            val permissions = setOf(
                HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)
            )

            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (!granted.containsAll(permissions)) {
                 return Result.failure(Exception("Permissions not granted"))
            }

            val request = ReadRecordsRequest(
                recordType = ActiveCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay.toJavaInstant(), endOfDay.toJavaInstant())
            )

            val response = healthConnectClient.readRecords(request)
            val totalCalories = response.records.sumOf { it.energy.inKilocalories }

            Result.success(
                DailyCalories(
                    date = date,
                    caloriesKcal = totalCalories,
                    sourceId = "health_connect",
                    fetchedAt = Clock.systemUTC().millis()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

actual class HealthServiceFactory(private val context: Context) {
    actual fun create(): HealthService {
        return AndroidHealthService(context)
    }
}
