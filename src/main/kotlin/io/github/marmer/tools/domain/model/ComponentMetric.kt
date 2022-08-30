package io.github.marmer.tools.domain.model

import java.time.LocalDate

data class ComponentMetric(
    val key: String,
    val name: String,
    val date: LocalDate,
    val measures: List<Measure>
)

data class Measure(val metric: String, val value: Int)
