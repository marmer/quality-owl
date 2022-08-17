package io.github.marmer.tools

import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DataFetchJob {

    @Scheduled(every = "10s")
    fun fetchData() {
        Log.infof("Weeheh!", Blubba("a", "b######"))
    }
}

data class Blubba(val k1: String, val k2: String)
