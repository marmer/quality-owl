package io.github.marmer.tools.adapter.persistence

import io.github.marmer.tools.domain.model.ComponentMetric
import io.github.marmer.tools.domain.model.Measure
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MetricsPersistencePortAdapter() : MetricsPersistencePort {

    private var componentMetricsByKey: Map<String, ComponentMetricDbo> = emptyMap()

    @Synchronized
    override fun persist(componentMetric: ComponentMetric) {
        componentMetricsByKey +=
            Pair(componentMetric.key, getComponentMetricDboBy(componentMetric)
                .run {
                    copy(
                        measuresByDate = measuresByDate + Pair(
                            componentMetric.date,
                            componentMetric.measures
                        )
                    )
                })
    }

    private fun getComponentMetricDboBy(componentMetric: ComponentMetric) =
        (componentMetricsByKey.get(componentMetric.key)
            ?: ComponentMetricDbo(
                componentMetric.key,
                componentMetric.name,
                componentMetric.date,
                emptyMap()
            ))
}

private data class ComponentMetricDbo(
    val key: String,
    val name: String,
    val date: LocalDate,
    val measuresByDate: Map<LocalDate, List<Measure>>
)
