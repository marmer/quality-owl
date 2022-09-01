package io.github.marmer.tools.domain.model

import java.time.LocalDate

data class ComponentDiff(
    val key: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val diffsByMetric: Map<String, MetricDiff>
)
