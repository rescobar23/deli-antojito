package com.famessa.deli_antojito.feature.batchclosing
import javax.inject.Qualifier

/** Worker dispatcher for strict civil-time resolution, provided by the application composition root. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BatchClosingWorker
