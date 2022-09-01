package io.github.marmer.tools.usecases.ports

import io.github.marmer.tools.domain.model.ComponentState

interface MetricsFetchPort {
    fun fetchComponentMetrics(): List<ComponentState>

}
