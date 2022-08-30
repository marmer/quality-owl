package io.github.marmer.tools.adapter.sonar

import io.github.marmer.tools.configuration.SonarConfig
import io.github.marmer.tools.test.Testdata
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import javax.inject.Inject

@QuarkusTest
internal class MetricsFetchPortAdapterTest {
    @Inject
    lateinit var underTest: MetricsFetchPortAdapter

    @InjectMock
    lateinit var sonarConfig: SonarConfig

    @InjectMock
    lateinit var sonarClient: SonarClient

    val testdata = Testdata(MetricsFetchPortAdapterTest::class)

    @Test
    fun `only metrics for configured Projects should be returned`() {
        // Preparation
        `when`(sonarConfig.projectIncludes).thenReturn(listOf("pk1", "pk2"))
        `when`(sonarConfig.metricKeys).thenReturn(listOf("mk1", "mk2"))
        val response1 = testdata.next(SonarClient.ComponentMetricsResponseDTO::class)
            .apply { component.key = "pk1" }

        val response2 = testdata.next(SonarClient.ComponentMetricsResponseDTO::class)
            .apply { component.key = "pk2" }
        `when`(sonarClient.getMetrics("pk1", "mk1,mk2")).thenReturn(response1)
        `when`(sonarClient.getMetrics("pk2", "mk1,mk2")).thenReturn(response2)


        // Execution
        val result = underTest.fetchComponentMetrics()

        // Assertion
        assertThat(
            result,
            contains(
                hasProperty("key", equalTo(response1.component.key)),
                hasProperty("key", equalTo(response2.component.key))
            )
        )
    }

    @Test
    fun `the metrics for all projects should be returned if no projects are configured`() {
        // Preparation
        `when`(sonarConfig.projectIncludes).thenReturn(emptyList())
        `when`(sonarConfig.metricKeys).thenReturn(listOf("mk1", "mk2"))
        `when`(sonarClient.getProjects(Mockito.anyList(), Mockito.anyInt()))
            .thenReturn(SonarClient.ProjectListResponseDTO()
                .apply {
                    components = listOf(
                        SonarClient.ProjectListResponseDTO.Component()
                            .apply { key = "kpi1" },
                        SonarClient.ProjectListResponseDTO.Component()
                            .apply { key = "kpi2" })
                })

        val response1 = testdata.next(SonarClient.ComponentMetricsResponseDTO::class)
            .apply { component.key = "kpi1" }
        val response2 = testdata.next(SonarClient.ComponentMetricsResponseDTO::class)
            .apply { component.key = "kpi2" }
        `when`(sonarClient.getMetrics("kpi1", "mk1,mk2")).thenReturn(response1)
        `when`(sonarClient.getMetrics("kpi2", "mk1,mk2")).thenReturn(response2)


        // Execution
        val result = underTest.fetchComponentMetrics()

        // Assertion
        assertThat(
            result,
            contains(
                hasProperty("key", equalTo(response1.component.key)),
                hasProperty("key", equalTo(response2.component.key))
            )
        )
    }

    @Test
    fun `errors on some projects should not affect other results`() {
        // Preparation
        `when`(sonarConfig.projectIncludes).thenReturn(listOf("pk1", "pk2", "pk3"))
        `when`(sonarConfig.metricKeys).thenReturn(listOf("mk1", "mk2"))
        val response1 = testdata.next(SonarClient.ComponentMetricsResponseDTO::class)
            .apply { component.key = "pk1" }

        val response3 = testdata.next(SonarClient.ComponentMetricsResponseDTO::class)
            .apply { component.key = "pk3" }
        `when`(sonarClient.getMetrics("pk1", "mk1,mk2")).thenReturn(response1)
        `when`(sonarClient.getMetrics("pk2", "mk1,mk2")).thenThrow(RuntimeException::class.java)
        `when`(sonarClient.getMetrics("pk3", "mk1,mk2")).thenReturn(response3)


        // Execution
        val result = underTest.fetchComponentMetrics()

        // Assertion
        assertThat(
            result,
            contains(
                hasProperty("key", equalTo(response1.component.key)),
                hasProperty("key", equalTo(response3.component.key)),
            )
        )
    }

}
