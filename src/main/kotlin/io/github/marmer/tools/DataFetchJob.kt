package io.github.marmer.tools

import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class DataFetchJob(var sonarConfig: SonarConfig) {

    /**
     * Hacky Workaround because constructor injection does not work for RestClient
     */
    @Inject
    @field: RestClient
    lateinit internal var sonarClient: SonarClient

    @Scheduled(every = "{sonar.fetch-interval-cron}")
    fun fetchData() {
        Log.infof("Weeheh ... Fetching from: ${sonarConfig.url()}")
        Log.infof("Weeheh ... For User or Token: ${sonarConfig.usernameOrToken().orElse("Mööp")}")
//        Log.info(sonarClient.getCoverage("marmer_code-brunch-calc", "coverage"))
    }
}

