package io.github.marmer.tools.adapter.persistence

import io.github.marmer.tools.domain.model.ComponentState
import io.github.marmer.tools.domain.model.Measure
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MetricsPersistencePortAdapter() : MetricsPersistencePort {

    private var componentMetricsByKey: Map<String, ComponentMetricDbo> = emptyMap()

    @Synchronized
    override fun persist(componentState: ComponentState) {
        componentMetricsByKey +=
            Pair(componentState.key, getComponentMetricDboBy(componentState)
                .run {
                    copy(
                        measuresByDate = measuresByDate + Pair(
                            componentState.date,
                            componentState.measures
                        )
                    )
                })
    }

    override fun findOneComponentStatePerComponentClosestTo(start: LocalDate): List<ComponentState> {
        TODO("Not yet implemented")

        // TODO: marmer 31.08.2022 Exact finding
        // TODO: marmer 31.08.2022 Close findings

        // TODO: marmer 31.08.2022 no finding at all => not served

        // TODO: marmer 31.08.2022 only one finding closer to start Date => no end date and end
        // TODO: marmer 31.08.2022 only one finding closer to end Date => no start date and start
        // TODO: marmer 31.08.2022 only one finding in the middle of start and end date => no start date
    }

    private fun getComponentMetricDboBy(componentState: ComponentState) =
        (componentMetricsByKey.get(componentState.key)
            ?: ComponentMetricDbo(
                componentState.key,
                componentState.name,
                componentState.date,
                emptyMap()
            ))
}

private data class ComponentMetricDbo(
    val key: String,
    val name: String,
    val date: LocalDate,
    val measuresByDate: Map<LocalDate, List<Measure>>
)
