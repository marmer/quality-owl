package io.github.marmer.tools.usecases

import io.github.marmer.tools.domain.model.ComponentDiff
import io.github.marmer.tools.domain.model.Measure
import io.github.marmer.tools.domain.model.MetricDiff
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DiffLoader(private val metricsPersistencePort: MetricsPersistencePort) {
    fun getDiffsBetween(
        startDateInclusive: LocalDate,
        endDateInclusive: LocalDate
    ): List<ComponentDiff> {

        val startDiffs = metricsPersistencePort
            .findOneComponentStatePerComponentClosestTo(startDateInclusive)

        val endDiffs = metricsPersistencePort
            .findOneComponentStatePerComponentClosestTo(endDateInclusive)

        return (startDiffs + endDiffs)
            .groupBy { it.key }
            .map { (key, states) ->
                val (firstState) = states
                val secondState = if (states.size > 1) states[1] else firstState
                ComponentDiff(
                    key,
                    secondState.name,
                    firstState.date,
                    secondState.date,
                    merge(firstState.measures, secondState.measures)
                )
            }.sortedBy { it.key }
    }

    private fun merge(
        startMeasure: List<Measure>,
        endMeasure: List<Measure>
    ): Map<String, MetricDiff> {
        val startMeasuresByKey = startMeasure.map { Pair(it.metric, it.value) }.toMap()
        val endMeasuresByKey = endMeasure.map { Pair(it.metric, it.value) }.toMap()

        return (startMeasuresByKey + endMeasuresByKey).keys
            .map {
                Pair(
                    it,
                    MetricDiff(startMeasuresByKey.get(it), endMeasuresByKey.get(it))
                )
            }
            .toMap()
    }


}

