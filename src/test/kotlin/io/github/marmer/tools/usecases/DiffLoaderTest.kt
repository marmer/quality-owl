package io.github.marmer.tools.usecases

import io.github.marmer.tools.domain.model.ComponentState
import io.github.marmer.tools.domain.model.Measure
import io.github.marmer.tools.domain.model.MetricDiff
import io.github.marmer.tools.test.Testdata
import io.github.marmer.tools.test.asserter.Assertions.assertThat
import io.github.marmer.tools.usecases.ports.MetricsPersistencePort
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDate
import java.util.function.Consumer


internal class DiffLoaderTest {
    private val metricsPersistencePort: MetricsPersistencePort =
        Mockito.mock(MetricsPersistencePort::class.java)

    private val underTest: DiffLoader = DiffLoader(metricsPersistencePort)

    private val testdata = Testdata(DiffLoaderTest::class)

    @Test
    fun `exact matches for start and date should serve the results in diffs`() {
        // Preparation
        val startDate = LocalDate.of(2022, 2, 2)
        val endDate = LocalDate.of(2022, 3, 3)

        val k2Start = newComponentState("k2", startDate)
        val k1Start = newComponentState("k1", startDate)
        val k3Start = newComponentState("k3", startDate)

        val k1End = newComponentState("k1", endDate)
        val k2End = newComponentState("k2", startDate)

        `when`(metricsPersistencePort.findOneComponentStatePerComponentClosestTo(startDate))
            .thenReturn(
                listOf(
                    k2Start,
                    k1Start,
                    k3Start,
                )
            )

        `when`(metricsPersistencePort.findOneComponentStatePerComponentClosestTo(endDate))
            .thenReturn(
                listOf(
                    k1End,
                    k2End
                )
            )

        // Execution
        val result = underTest.getDiffsBetween(startDate, endDate)

        // Assertion
        assertThat(result).satisfiesExactly(
            Consumer {
                assertThat(it)
                    .hasKey("k1")
                    .hasName(k1End.name)
                    .hasStartDate(startDate)
                    .hasEndDate(endDate)
            },
            Consumer {
                assertThat(it)
                    .hasKey("k2")
                    .hasName(k2End.name)
                    .hasStartDate(startDate)
                    .hasEndDate(startDate)
            },
            Consumer {
                assertThat(it)
                    .hasKey("k3")
                    .hasName(k3Start.name)
                    .hasStartDate(startDate)
                    .hasEndDate(startDate)
            }
        )
    }

    @Test
    fun `Should return empty list if nothing was found`() {
        // Preparation
        val startDate = LocalDate.of(2022, 2, 2)
        val endDate = LocalDate.of(2022, 3, 3)


        `when`(metricsPersistencePort.findOneComponentStatePerComponentClosestTo(startDate))
            .thenReturn(emptyList())

        `when`(metricsPersistencePort.findOneComponentStatePerComponentClosestTo(endDate))
            .thenReturn(emptyList())

        // Execution
        val result = underTest.getDiffsBetween(startDate, endDate)

        // Assertion
        assertThat(result).isEmpty()
    }

    private fun newComponentState(componentKey: String, start: LocalDate) =
        testdata.next(ComponentState::class)
            .run { copy(key = componentKey, date = start, measures = measures) }

    // TODO: marmer 31.08.2022 Check Metrics merge

    // TODO: marmer 01.09.2022 same metric
    // TODO: marmer 01.09.2022 start metric only
    // TODO: marmer 01.09.2022 end metric only


    @Test
    fun `should merge metrics correctly`() {
        // Preparation
        val startDate = LocalDate.of(2022, 2, 2)
        val endDate = LocalDate.of(2022, 3, 3)

        val k1Start = newComponentState("k1", startDate)
            .run { copy(measures = listOf(Measure("cats", 1), Measure("dogs", 2))) }

        val k1End = newComponentState("k1", endDate)
            .run { copy(measures = listOf(Measure("cats", 3), Measure("birds", 4))) }

        `when`(metricsPersistencePort.findOneComponentStatePerComponentClosestTo(startDate))
            .thenReturn(listOf(k1Start))

        `when`(metricsPersistencePort.findOneComponentStatePerComponentClosestTo(endDate))
            .thenReturn(listOf(k1End))

        // Execution
        val result = underTest.getDiffsBetween(startDate, endDate)

        // Assertion
        assertThat(result)
            .satisfiesExactly(
                Consumer {
                    assertThat(it)
                        .hasKey("k1")
                        .extracting { it.diffsByMetric }
                        .satisfies(Consumer { diffsByMetric ->
                            assertThat(diffsByMetric)
                                .containsExactly(
                                    entry("cats", MetricDiff(1, 3)),
                                    entry("dogs", MetricDiff(2, null)),
                                    entry("birds", MetricDiff(null, 4)),
                                )
                        })
                },
            )


    }

}
