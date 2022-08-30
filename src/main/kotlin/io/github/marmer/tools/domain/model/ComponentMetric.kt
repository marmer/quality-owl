package io.github.marmer.tools.domain.model

data class ComponentMetric(val key: String, val name: String, val measures: List<Measure>)

data class Measure(val metric: String, val value: Int)
