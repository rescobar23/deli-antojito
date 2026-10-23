package com.famessa.deli_antojito.data.time

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.famessa.deli_antojito.domain.time.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.TimeZone

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PedidoDayObserverTest {
    @Test fun timerAndResumeRefreshPublishNewDayAndCollectionCleansUp() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val calculator = LocalDayCalculator { TimeZone.getTimeZone("UTC") }
        val boundary = calculator.midnight(LocalDateParts(2026,10,22))
        var now = boundary - 20
        val provider = LocalBusinessDayProvider(context, { now }, { TimeZone.getTimeZone("UTC") })
        val values = mutableListOf<BusinessDay>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            provider.observeDay().take(2).toList(values)
        }
        runCurrent()
        assertEquals(LocalDateParts(2026,10,21), values.single().fecha)
        now = boundary
        advanceTimeBy(20); runCurrent()
        assertEquals(LocalDateParts(2026,10,22), values.last().fecha)
        job.join()
        // A new collection after completion proves that listener cleanup permits safe reuse.
        val resumed = mutableListOf<BusinessDay>()
        val resumeJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            provider.observeDay().take(2).toList(resumed)
        }
        runCurrent()
        now = calculator.midnight(LocalDateParts(2026,10,23))
        provider.refresh(); runCurrent()
        assertEquals(LocalDateParts(2026,10,23), resumed.last().fecha)
        resumeJob.join()
    }
}
