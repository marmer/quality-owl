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
        Log.infof("Weeheh ... Fetching from: ${sonarConfig.url}")
        Log.infof("Weeheh ... For User or Token: ${sonarConfig.usernameOrToken}")
        Log.infof("Weeheh ... For Projec Includes: ${sonarConfig.projectIncludes}")
        Log.infof("Weeheh ... For Metric Keys: ${sonarConfig.metricKeys}")

        sonarConfig.projectIncludes.map {
            sonarClient.getCoverage(
                it,
                sonarConfig.metricKeys.joinToString(",")
            )
        }.forEach(Log::infof)
    }
}

