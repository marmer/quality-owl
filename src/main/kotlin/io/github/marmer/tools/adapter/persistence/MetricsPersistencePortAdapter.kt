package io.github.marmer.tools.adapter.persistence

import io.github.marmer.tools.domain.model.ComponentMetric
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MetricsPersistencePortAdapter : MetricsPersistencePort {
    override fun persist(componentMetric: ComponentMetric) {
        TODO("Not yet implemented")
    }
}
