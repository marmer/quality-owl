package io.github.marmer.tools.acceptance

import io.github.marmer.tools.usecases.ClockProvider
import io.github.marmer.tools.usecases.MetricsUpdater
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import io.quarkus.test.junit.mockito.InjectMock
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject

@QuarkusTest
@TestProfile(MetricDiffAcceptenceTest.WithAuthAndProjectIncludes::class)
class MetricDiffAcceptenceTest {

    @Inject
    lateinit var underTest: MetricsUpdater

    @InjectMock
    lateinit var clockProvider: ClockProvider

    companion object {
        val clientAndServer: ClientAndServer = ClientAndServer.startClientAndServer()


        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            clientAndServer.stop()
        }
    }

    @BeforeEach
    fun setUp() {
        clientAndServer.reset()


    }


    @Test
    fun `Metrics and diffs should be given correctly for all metrics and the requested project`() {
        // Preparation
        val firstNow = nowIs(2011, 7, 9)
        firstProjectMetricsAre(
            """
                {
                  "component": {
                    "key": "fancy-beaver.in:da-house",
                    "name": "fbidh",
                    "description": "Big bouncing beaver belly",
                    "qualifier": "TRK",
                    "measures": [
                      {
                        "metric": "code_smells",
                        "value": "534",
                        "bestValue": false
                      },
                      {
                        "metric": "bugs",
                        "value": "3",
                        "bestValue": false
                      }
                    ]
                  }
                }
            """.trimIndent()
        )

        val secondNow = nowIs(2011, 10, 9)
        firstProjectMetricsAre(
            """
                {
                  "component": {
                    "key": "fancy-beaver.in:da-house",
                    "name": "fbidh",
                    "description": "Big bouncing beaver belly",
                    "qualifier": "TRK",
                    "measures": [
                      {
                        "metric": "code_smells",
                        "value": "534",
                        "bestValue": false
                      },
                      {
                        "metric": "bugs",
                        "value": "2",
                        "bestValue": false
                      }
                    ]
                  }
                }
            """.trimIndent()
        )


        // Execution
        val result = underTest.updateMetrics()

        // Assertion
        RestAssured.given()
            .`when`()
            .queryParam("limit", "1")
            .queryParam("since", "2011-06-08")
            .get("/diff").then().statusCode(200).body(
                CoreMatchers.`is`(
                    """
                        [
                          {
                            "key": "fancy-beaver.in:da-house",
                            "name": "fbidh",
                            "oldDate": "2011-07-09",
                            "recentDate": "2011-10-09",
                            "metricDiffs": [
                              {
                                "name": "code_smells",
                                "old": "534",
                                "recent": "576",
                                "delta": "42"
                              },
                              {
                                "name": "bugs",
                                "old": "1337",
                                "recent": "1234",
                                "delta": "-103"
                              }
                            ]
                          }
                        ]
                    """.trimIndent()
                )
            )

    }

    // TODO: marmer 26.08.2022 Trend from closest date in past to closest date to now
    // TODO: marmer 26.08.2022 QueryParam: bestImprovement
    // TODO: marmer 26.08.2022 QueryParam: bestHard

    // TODO: marmer 26.08.2022 Ranking for Unchanged
    // TODO: marmer 26.08.2022 Ranking for Changed
    // TODO: marmer 26.08.2022 Ranking for new

    // TODO: marmer 26.08.2022 Stats for Unchanged
    // TODO: marmer 26.08.2022 Stats for Changed
    // TODO: marmer 26.08.2022 Stats for new

    // TODO: marmer 26.08.2022 Metrics can be added and/or removed

    // TODO: marmer 23.08.2022 Cleanup!
    // TODO: marmer 23.08.2022 Persistence!
    // TODO: marmer 23.08.2022 Top X improved (only if old data exist)
    // TODO: marmer 23.08.2022 Top X At all (
    // TODO: marmer 23.08.2022 Only Changed Projects?
    // TODO: marmer 23.08.2022 UI? ... maybe ;)
    // TODO: marmer 23.08.2022 Run with Docker

    private fun firstProjectMetricsAre(responseBody: String) {
        clientAndServer.`when`(
            HttpRequest.request().withMethod("GET").withPath("/blub/measures/component")
                .withHeader("Authentication", "Basic dW46cHc=")
                .withQueryStringParameter("component", "fancy-beaver.in:da-house")
        ).respond(
            HttpResponse.response().withBody(responseBody)
        )
    }

    private fun nowIs(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 10,
        minute: Int = 0,
        second: Int = 0,
        nano: Int = 0
    ): ZonedDateTime {
        val now = ZonedDateTime.of(
            year, month, day, hour, minute, second, nano, ZonedDateTime.now().zone
        )
        Mockito.`when`(clockProvider.provideClock()).thenReturn(
            Clock.fixed(
                now.toInstant(), ZonedDateTime.now().zone
            )
        )
        return now;
    }

    class WithAuthAndProjectIncludes : QuarkusTestProfile {
        override fun getConfigOverrides(): Map<String, String> = mapOf(
            Pair("sonar.url", "https://localhost:${clientAndServer.port}/blub"),
            Pair("sonar.username-or-token", "un"),
            Pair("sonar.password", "pw"),
            Pair("sonar.project-includes", "fancy-beaver.in:da-house,troubadix:the-barde"),
            Pair("sonar.metric-keys", "bugs,code_smells"),
        )
    }
}
