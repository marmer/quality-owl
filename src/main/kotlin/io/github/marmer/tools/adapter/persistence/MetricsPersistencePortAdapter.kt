package io.github.marmer.tools.adapter.persistence

import io.github.marmer.tools.domain.model.ComponentState
import io.github.marmer.tools.domain.model.Measure
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import java.time.LocalDate
import java.time.Period
import javax.enterprise.context.ApplicationScoped
import kotlin.math.abs

@ApplicationScoped
class MetricsPersistencePortAdapter : MetricsPersistencePort {

    private var componentMetricsByKey: Map<String, ComponentDbo> = emptyMap()

    @Synchronized
    override fun persist(componentState: ComponentState) {
        componentMetricsByKey =
            componentMetricsByKey + Pair(componentState.key, getComponentMetricDboBy(componentState)
                .run {
                    copy(
                        measuresByDate = measuresByDate + Pair(
                            componentState.date,
                            componentState.measures
                        )
                    )
                })
    }

    override fun findOneComponentStatePerComponentClosestTo(date: LocalDate): List<ComponentState> {
        return componentMetricsByKey.values
            .map {
                val closestDate = it.measuresByDate.keys.closestTo(date)

                ComponentState(
                    it.key,
                    it.name,
                    closestDate,
                    it.measuresByDate[closestDate] ?: emptyList()
                )
            }

        // TODO: marmer 31.08.2022 Exact finding
        // TODO: marmer 31.08.2022 Close findings

        // TODO: marmer 31.08.2022 no finding at all => not served

        // TODO: marmer 31.08.2022 only one finding closer to start Date => no end date and end
        // TODO: marmer 31.08.2022 only one finding closer to end Date => no start date and start
        // TODO: marmer 31.08.2022 only one finding in the middle of start and end date => no start date
    }

    private fun getComponentMetricDboBy(componentState: ComponentState) =
        (componentMetricsByKey.get(componentState.key)
            ?: ComponentDbo(
                componentState.key,
                componentState.name,
                emptyMap()
            ))
}

private fun Set<LocalDate>.closestTo(date: LocalDate): LocalDate {
    return minByOrNull {
        abs(
            Period.between(date, it).days
        )
    }!!
}

private data class ComponentDbo(
    val key: String,
    val name: String,
    val measuresByDate: Map<LocalDate, List<Measure>>
)
