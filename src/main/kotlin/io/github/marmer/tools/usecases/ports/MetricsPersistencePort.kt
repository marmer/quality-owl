package io.github.marmer.tools.usecases.ports

import io.github.marmer.tools.domain.model.ComponentMetric

interface MetricsPersistencePort {
    fun persist(componentMetric: ComponentMetric)

}
