package io.github.marmer.tools.usecases.ports

import io.github.marmer.tools.domain.model.ComponentState
import java.time.LocalDate

interface MetricsPersistencePort {
    fun persist(componentState: ComponentState)
    fun findOneComponentStatePerComponentClosestTo(start: LocalDate): List<ComponentState>

}
