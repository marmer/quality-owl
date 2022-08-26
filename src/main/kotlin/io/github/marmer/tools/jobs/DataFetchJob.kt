package io.github.marmer.tools.jobs

import io.github.marmer.tools.adapter.sonar.SonarClient
import io.github.marmer.tools.configuration.SonarConfig
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class DataFetchJob(private val sonarConfig: SonarConfig) {


    /**
     * Hacky Workaround because constructor injection does not work for RestClient
     */
    @Inject
    @field: RestClient
    internal lateinit var sonarClient: SonarClient

    @Scheduled(every = "{sonar.fetch-interval-cron}")
    fun fetchData() {


        if (sonarConfig.projectIncludes.isEmpty())
            getAllProjects().components
                .map { it.key }
                .map { getMetrics(it) }
                .forEach { Log.info(it) }
        else {
            sonarConfig.projectIncludes.map {
                getMetrics(it)
            }.forEach { Log.info(it) }
        }
    }

    private fun getAllProjects() = sonarClient.getProjects(listOf("TRK", "APP"), 500)

    private fun getMetrics(it: String) =
        sonarClient.getMetrics(
            it,
            sonarConfig.metricKeys.joinToString(",")
        )


}
