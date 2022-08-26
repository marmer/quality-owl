package io.github.marmer.tools.usecases

import java.time.Clock
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class ClockProvider {
    @Produces
    fun provideClock() = Clock.systemUTC()
}
