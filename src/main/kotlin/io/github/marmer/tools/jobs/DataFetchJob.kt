package io.github.marmer.tools.jobs

import io.github.marmer.tools.usecases.ports.MetricsFetchPort
import io.quarkus.scheduler.Scheduled
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DataFetchJob(
    private val metricsFetchPort: MetricsFetchPort,
) {

    val lock: Lock = ReentrantLock()

    @Scheduled(every = "{sonar.fetch-interval-cron}")
    fun fetchData() {
        if (lock.tryLock())
            try {
                metricsFetchPort.fetchComponentMetrics()
            } finally {
                lock.unlock()
            }
    }

}
