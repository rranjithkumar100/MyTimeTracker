package ai.health

import platform.Foundation.NSDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDateComponents
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKStatisticsQuery
import platform.HealthKit.HKStatisticsOptionCumulativeSum
import platform.HealthKit.HKUnit
import platform.HealthKit.HKQuantity
import platform.Foundation.NSError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.cinterop.cValue
import platform.Foundation.dateWithTimeIntervalSince1970

class IosHealthService : HealthService {
    private val healthStore = HKHealthStore()

    override suspend fun getCalories(date: String): Result<DailyCalories> {
        if (!HKHealthStore.isHealthDataAvailable()) {
            return Result.failure(Exception("HealthKit not available"))
        }

        val type = HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierActiveEnergyBurned)
            ?: return Result.failure(Exception("Active Energy Type not available"))

        // Request authorization (this usually should be done once, but we can check status)
        // Since this is a background sync potentially, we assume auth is done or we fail.
        // But the prompt says "request only read permission".
        // In iOS, requestAuthorizationToShareTypes returns void and calls completion.

        return suspendCancellableCoroutine { continuation ->
             val typesToRead = setOf(type)
             healthStore.requestAuthorizationToShareTypes(
                 shareTypes = null,
                 readTypes = typesToRead as Set<HKObjectType>
             ) { success, error ->
                 if (!success) {
                     continuation.resume(Result.failure(Exception(error?.localizedDescription ?: "Authorization failed")))
                     return@requestAuthorizationToShareTypes
                 }

                 // Query
                 val calendar = NSCalendar.currentCalendar

                 // Parse date string YYYY-MM-DD
                 val parts = date.split("-")
                 if (parts.size != 3) {
                      continuation.resume(Result.failure(Exception("Invalid date format")))
                      return@requestAuthorizationToShareTypes
                 }
                 val year = parts[0].toInt()
                 val month = parts[1].toInt()
                 val day = parts[2].toInt()

                 val components = NSDateComponents().apply {
                     setYear(year.toLong())
                     setMonth(month.toLong())
                     setDay(day.toLong())
                 }

                 val startDate = calendar.dateFromComponents(components)!!
                 val endDate = calendar.dateByAddingUnit(NSCalendarUnitDay, 1, startDate, 0u)!!

                 val predicate = platform.HealthKit.HKQuery.predicateForSamplesWithStartDate(startDate, endDate, 0u)

                 val query = HKStatisticsQuery(
                     quantityType = type,
                     quantitySamplePredicate = predicate,
                     options = HKStatisticsOptionCumulativeSum
                 ) { _, result, error ->
                     if (error != null) {
                         continuation.resume(Result.failure(Exception(error.localizedDescription)))
                     } else {
                         val quantity = result?.sumQuantity()
                         val calories = quantity?.doubleValueForUnit(HKUnit.kilocalorieUnit()) ?: 0.0

                         continuation.resume(Result.success(
                             DailyCalories(
                                 date = date,
                                 caloriesKcal = calories,
                                 sourceId = "health_kit",
                                 fetchedAt = (NSDate().timeIntervalSince1970 * 1000).toLong()
                             )
                         ))
                     }
                 }
                 healthStore.executeQuery(query)
             }
        }
    }
}

actual class HealthServiceFactory {
    actual fun create(): HealthService {
        return IosHealthService()
    }
}
