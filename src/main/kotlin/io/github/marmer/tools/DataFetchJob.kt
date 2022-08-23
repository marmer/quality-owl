package io.github.marmer.tools

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

// TODO: marmer 23.08.2022 Tests
// TODO: marmer 23.08.2022 Cleanup!
// TODO: marmer 23.08.2022 Persistence!
// TODO: marmer 23.08.2022 Top X improved (only if old data exist)
// TODO: marmer 23.08.2022 Top X At all (
// TODO: marmer 23.08.2022 Only Changed Projects?
// TODO: marmer 23.08.2022 UI? ... maybe ;)
// TODO: marmer 23.08.2022 Run with Docker
