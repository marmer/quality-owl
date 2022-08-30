package io.github.marmer.tools.usecases

import io.github.marmer.tools.usecases.ports.MetricsFetchPort
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import javax.inject.Singleton

@Singleton
class MetricsUpdater(
    val metricsFetchPort: MetricsFetchPort,
    val metricsPersistencePort: MetricsPersistencePort
) {

    fun updateMetrics() =
        metricsFetchPort.fetchComponentMetrics()
            .forEach { metricsPersistencePort.persist(it) }

}
