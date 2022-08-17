package io.github.marmer.tools

import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class DataFetchJob constructor() {

    /**
     * Hacky Workaround because constructor injection does not work for RestClient
     */
    @Inject
    @field: RestClient
    lateinit internal var sonarClient: SonarClient

    @Scheduled(every = "10s")
    fun fetchData() {
        Log.infof("Weeheh!")
        Log.info(sonarClient.getCoverage("marmer_code-brunch-calc", "coverage"))
    }
}

