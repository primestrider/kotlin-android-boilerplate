package com.primestrider.kotlinstarter.feature.example.sdk.calendar

import androidx.annotation.StringRes
import com.primestrider.kotlinstarter.R
import java.util.Calendar
import java.util.TimeZone

private const val MILLIS_PER_HOUR = 3_600_000L
private const val START_HOUR = 9

/** A preset booking: its title, where it takes place and how long it lasts. */
enum class Booking(
    @StringRes val title: Int,
    val location: String,
    val hours: Int,
) {
    FLIGHT(R.string.sdk_calendar_booking_flight, "Soekarno-Hatta T3", 2),
    DOCTOR(R.string.sdk_calendar_booking_doctor, "RS Pondok Indah", 1),
    MOVIE(R.string.sdk_calendar_booking_movie, "CGV Grand Indonesia", 3),
}

/** When the booking happens, as a number of days from today (always at 09:00 local). */
enum class BookingDay(
    @StringRes val label: Int,
    val days: Int,
) {
    TOMORROW(R.string.sdk_calendar_day_tomorrow, 1),
    IN_3_DAYS(R.string.sdk_calendar_day_3_days, 3),
    NEXT_WEEK(R.string.sdk_calendar_day_next_week, 7),
}

/** 09:00 in [zone] on the day [days] after [now]. `java.util.Calendar`, because java.time needs API 26. */
fun bookingStart(
    now: Long,
    days: Int,
    zone: TimeZone,
): Long =
    Calendar
        .getInstance(zone)
        .apply {
            timeInMillis = now
            add(Calendar.DAY_OF_MONTH, days)
            set(Calendar.HOUR_OF_DAY, START_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

/** The end of [booking] when it starts at [start]. */
fun bookingEnd(
    start: Long,
    booking: Booking,
): Long = start + booking.hours * MILLIS_PER_HOUR
