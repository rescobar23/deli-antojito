package com.famessa.deli_antojito.data.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.famessa.deli_antojito.domain.time.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/** JVM calendar calculations independent of Android event delivery; uses actual local midnight boundaries. */
class LocalDayCalculator(private val zone: () -> TimeZone = { TimeZone.getDefault() }) {
    /** First valid instant of a civil date; a midnight gap is normalized within the selected zone. */
    fun midnight(date: LocalDateParts): Long = midnight(date, zone())
    private fun civilCalendar(date: LocalDateParts): Calendar {
        require(date.year in 1..9999)
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            isLenient = false
            clear()
            set(date.year, date.month - 1, date.day, 0, 0, 0)
            timeInMillis // Validate the date independently of any local midnight gap.
        }
    }
    private fun midnight(date: LocalDateParts, selectedZone: TimeZone): Long {
        civilCalendar(date)
        return Calendar.getInstance(selectedZone).apply {
            isLenient = true
            clear()
            set(date.year, date.month - 1, date.day, 0, 0, 0)
        }.timeInMillis
    }
    /** Next civil date's boundary, rather than adding 24 hours or preserving a shifted midnight hour. */
    fun nextMidnight(date: LocalDateParts): Long = nextMidnight(date, zone())
    private fun nextMidnight(date: LocalDateParts, selectedZone: TimeZone): Long {
        val next = civilCalendar(date).apply { add(Calendar.DAY_OF_MONTH, 1) }
        return midnight(LocalDateParts(next.get(Calendar.YEAR), next.get(Calendar.MONTH) + 1, next.get(Calendar.DAY_OF_MONTH)), selectedZone)
    }
    /** Computes one internally consistent interval using a snapshot of the current zone. */
    fun day(now: Long): BusinessDay {
        val selectedZone = zone()
        val calendar = Calendar.getInstance(selectedZone).apply { timeInMillis = now }
        val date = LocalDateParts(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
        return BusinessDay(date, midnight(date, selectedZone), nextMidnight(date, selectedZone))
    }
    /** Converts inclusive optional civil bounds, rejecting invalid or inverted intervals. */
    fun range(from: LocalDateParts?, through: LocalDateParts?): EpochRange {
        val selectedZone = zone()
        val start = from?.let { midnight(it, selectedZone) }
        val end = through?.let { nextMidnight(it, selectedZone) }
        require(start == null || end == null || start < end)
        return EpochRange(start, end)
    }
}

/** API-25-compatible local clock; listeners live only while a day flow is collected. */
@Singleton
class LocalBusinessDayProvider @Inject constructor(@ApplicationContext private val context: Context) : BusinessDayProvider {
    private var calculator = LocalDayCalculator()
    private var currentTime: () -> Long = System::currentTimeMillis
    internal constructor(context: Context, now: () -> Long, zone: () -> TimeZone) : this(context) {
        currentTime = now
        calculator = LocalDayCalculator(zone)
    }
    private val resumes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override fun nowMillis(): Long = currentTime()
    override fun range(from: LocalDateParts?, through: LocalDateParts?) = calculator.range(from, through)
    override fun refresh() { resumes.tryEmit(Unit) }
    override fun observeDay(): Flow<BusinessDay> = callbackFlow {
        fun publish() { trySend(calculator.day(nowMillis())) }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) { publish() }
        }
        ContextCompat.registerReceiver(context, receiver, IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }, ContextCompat.RECEIVER_NOT_EXPORTED)
        publish()
        val timer = launch {
            while (true) {
                val now = nowMillis()
                // Recompute the next boundary on every event or at most once per minute if the clock moves.
                delay((calculator.day(now).finExclusivo - now).coerceIn(1L, 60_000L))
                publish()
            }
        }
        val resumeJob = launch { resumes.collect { publish() } }
        awaitClose { timer.cancel(); resumeJob.cancel(); context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()
}
