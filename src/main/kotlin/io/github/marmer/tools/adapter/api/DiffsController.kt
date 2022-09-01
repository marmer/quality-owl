package io.github.marmer.tools.adapter.api

import io.github.marmer.tools.domain.model.ComponentDiff
import io.github.marmer.tools.domain.model.MetricDiff
import io.github.marmer.tools.usecases.DiffLoader
import java.time.Clock
import java.time.LocalDate
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Path("/diffs")
@Produces(MediaType.APPLICATION_JSON)
class DiffsController(private val diffLoader: DiffLoader, private val clock: Clock) {
    @GET
    fun getDiffs(@QueryParam("limit") limit: Int, @QueryParam("since") startDate: LocalDate) {
        diffLoader.getDiffsBetween(startDate, LocalDate.now(clock))
            .map {
                ComponentDiffResponseDto(
                    it
                )
            }
    }

    data class ComponentDiffResponseDto(
        val key: String,
        val name: String,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val metricDiffs: List<MetricDiffDto>,
    ) {
        constructor(componentDiff: ComponentDiff) :
                this(componentDiff.key,
                    componentDiff.name,
                    componentDiff.startDate,
                    componentDiff.endDate,
                    componentDiff.diffsByMetric.map { (metricName, diff): Map.Entry<String, MetricDiff> ->
                        MetricDiffDto(
                            metricName,
                            diff
                        )
                    })

        data class MetricDiffDto(
            val name: String,
            val start: String,
            val end: String,
            val deltaAbsolut: String,
            val deltaRelativeToStart: String,
        ) {
            constructor(
                name: String,
                diff: MetricDiff
            ) : this(
                name,
                diff.start.toString(),
                diff.end.toString(),
                diff.deltaAbsolute.toString(),
                String.format("%f.1", diff.deltaRelativeToStartInPercent)
            )
        }
    }

}
