package io.github.marmer.tools.adapter.persistence

import io.github.marmer.tools.domain.model.ComponentState
import io.github.marmer.tools.test.Testdata
import io.github.marmer.tools.test.asserter.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.function.Consumer

internal class MetricsPersistencePortAdapterTest {

    private var underTest: MetricsPersistencePortAdapter = MetricsPersistencePortAdapter()
    private val testdata: Testdata = Testdata(MetricsPersistencePortAdapterTest::class)

    @Test
    fun `should find closest Date for each componentMetric`() {
        // Preparation
        val date = testdata.next<LocalDate>()

        val exactState = testdata.next<ComponentState>()
            .copy(key = "k1", date = date)
        val laterState = testdata.next<ComponentState>()
            .copy(key = "k2", date = date.plusDays(1))
        val earlierState = testdata.next<ComponentState>()
            .copy(key = "k3", date = date.minusDays(1))
        val farLaterState = testdata.next<ComponentState>()
            .copy(key = "k4", date = date.minusYears(1))
        val farEarlierState = testdata.next<ComponentState>()
            .copy(key = "k5", date = date.minusYears(1))

        underTest.apply {
            persist(exactState)

            persist(
                testdata.next<ComponentState>()
                    .copy(key = "k2", name = "toFarEarlier", date.minusDays(2))
            )
            persist(
                laterState
            )
            persist(
                testdata.next<ComponentState>()
                    .copy(key = "k2", name = "toFarLater", date.plusDays(2))
            )

            persist(
                testdata.next<ComponentState>()
                    .copy(key = "k3", name = "toFarEarlier", date.minusDays(2))
            )
            persist(
                earlierState
            )
            persist(
                testdata.next<ComponentState>()
                    .copy(key = "k3", name = "toFarLater", date.plusDays(2))
            )

            persist(
                farLaterState
            )

            persist(
                farEarlierState
            )

        }

        // Execution
        val result = underTest.findOneComponentStatePerComponentClosestTo(date)

        // Assertion
        assertThat(result).satisfiesExactlyInAnyOrder(
            Consumer {
                assertThat(exactState)
                    .hasKey(exactState.key)
                    .hasDate(exactState.date)
                    .hasMeasures(exactState.measures)
            },
            Consumer {
                assertThat(laterState)
                    .hasKey(laterState.key)
                    .hasDate(laterState.date)
                    .hasMeasures(laterState.measures)
            },
            Consumer {
                assertThat(earlierState)
                    .hasKey(earlierState.key)
                    .hasDate(earlierState.date)
                    .hasMeasures(earlierState.measures)
            },
            Consumer {
                assertThat(farLaterState)
                    .hasKey(farLaterState.key)
                    .hasDate(farLaterState.date)
                    .hasMeasures(farLaterState.measures)
            },
            Consumer {
                assertThat(farEarlierState)
                    .hasKey(farEarlierState.key)
                    .hasDate(farEarlierState.date)
                    .hasMeasures(farEarlierState.measures)
            },
        )
    }

    @Test
    fun `should not find any component metric if none exist`() {
        // Preparation
        val date = testdata.next<LocalDate>()

        // Execution
        val result = underTest.findOneComponentStatePerComponentClosestTo(date)

        // Assertion
        assertThat(result).isEmpty()
    }

    // TODO: marmer 02.09.2022 Override existing dates

    // TODO: marmer 02.09.2022 Metrics should be stored too
    // TODO: marmer 02.09.2022 later name is used
}
