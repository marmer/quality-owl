package io.github.marmer.tools.adapter.sonar

import io.github.marmer.tools.configuration.SonarConfig
import io.github.marmer.tools.domain.model.ComponentState
import io.github.marmer.tools.domain.model.Measure
import io.github.marmer.tools.usecases.ports.MetricsFetchPort
import io.quarkus.logging.Log
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.Clock
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class MetricsFetchPortAdapter(private val sonarConfig: SonarConfig, val clock: Clock) :
    MetricsFetchPort {

    /**
     * Hacky Workaround because constructor injection does not work for RestClient
     */
    @Inject
    @field: RestClient
    private lateinit var sonarClient: SonarClient

    override fun fetchComponentMetrics(): List<ComponentState> {
        val projectKeys =
            if (sonarConfig.projectIncludes.isEmpty())
                getAllProjects().components.map { it.key }
            else {
                sonarConfig.projectIncludes
            }

        return projectKeys
            .mapNotNull { getMetrics(it) }
            .map { toMetric(it) }
    }

    private fun toMetric(it: SonarClient.ComponentMetricsResponseDTO) =
        ComponentState(
            it.component.key,
            it.component.name,
            LocalDate.now(clock),
            it.component.measures.map { measure ->
                Measure(
                    measure.metric,
                    measure.value.toInt()
                )
            })

    private fun getAllProjects() = sonarClient.getProjects(listOf("TRK", "APP"), 500)

    private fun getMetrics(projectKey: String): SonarClient.ComponentMetricsResponseDTO? =
        try {
            sonarClient.getMetrics(
                projectKey,
                sonarConfig.metricKeys.joinToString(",")
            )
        } catch (e: Exception) {
            Log.info("Not able to fetch Metrics for key ${projectKey}")
            null
        }
}

