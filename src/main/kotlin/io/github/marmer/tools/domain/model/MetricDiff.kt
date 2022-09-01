package io.github.marmer.tools.domain.model

data class MetricDiff(val start: Int?, val end: Int?) {
    val deltaAbsolute: Int?
        get() = if (end == null || start == null) null else end - start
    val deltaRelativeToStartInPercent: Double?
        get() = if (deltaAbsolute == null) null else 100 * deltaAbsolute!!.toDouble() / start!!.toDouble()
}
