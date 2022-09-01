package io.github.marmer.tools.domain.model

import java.time.LocalDate

data class ComponentState(
    val key: String,
    val name: String,
    val date: LocalDate,
    val measures: List<Measure>
)

