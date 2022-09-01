package io.github.marmer.tools.domain.model

import io.github.marmer.tools.test.asserter.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MetricDiffTest {
    @Test
    fun `should be able to calculate positive absolute delta`() {
        // Preparation

        // Execution
        val result = MetricDiff(23, 65)

        // Assertion
        assertEquals(42, result.deltaAbsolute)
    }

    @Test
    fun `should be able to calculate negarive absolute delta`() {
        // Preparation

        // Execution
        val result = MetricDiff(65, 23)

        // Assertion
        assertEquals(-42, result.deltaAbsolute)
    }

    @Test
    fun `should be able to calculate positive relative delta`() {
        // Preparation

        // Execution
        val result = MetricDiff(23, 65)

        // Assertion
        assertEquals(182.6, result.deltaRelativeToStartInPercent!!, 0.01)
    }

    @Test
    fun `should be able to calculate negarive relative delta`() {
        // Preparation

        // Execution
        val result = MetricDiff(65, 23)

        // Assertion
        assertEquals(-64.6, result.deltaRelativeToStartInPercent!!, 0.09)
    }

    @Test
    fun `there should be no delta without a start metric`() {
        // Preparation

        // Execution
        val result = MetricDiff(null, 23)

        // Assertion
        assertThat(result)
            .hasDeltaAbsolute(null)
            .hasDeltaRelativeToStartInPercent(null)
    }

}
