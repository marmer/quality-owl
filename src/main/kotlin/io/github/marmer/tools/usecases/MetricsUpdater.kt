package io.github.marmer.tools.usecases

import io.github.marmer.tools.usecases.ports.MetricsFetchPort
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import java.time.Clock
import javax.inject.Singleton

@Singleton
class MetricsUpdater(
    val clock: Clock,
    val metricsFetchPort: MetricsFetchPort,
    val metricsPersistencePort: MetricsPersistencePort
) {

    fun updateMetrics() =
        metricsFetchPort.fetchComponentMetrics()
            .forEach { metricsPersistencePort.persist(it) }

}
