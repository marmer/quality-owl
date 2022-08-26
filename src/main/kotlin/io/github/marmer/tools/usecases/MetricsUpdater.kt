package io.github.marmer.tools.usecases

import java.time.Clock
import javax.inject.Singleton

@Singleton
class MetricsUpdater(val clock: Clock) {

    fun updateMetrics(): Any {
        TODO("Not yet implemented ${clock}")
    }

}
