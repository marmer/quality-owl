package io.github.marmer.tools.jobs

import io.github.marmer.tools.usecases.ports.MetricsFetchPort
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import kotlin.concurrent.thread

internal class DataFetchJobTest {

    @Mock
    val metricsFetchPortMock: MetricsFetchPort = mock(MetricsFetchPort::class.java);

    @InjectMocks
    val underTest = DataFetchJob(metricsFetchPortMock);


//    @BeforeEach
//    internal fun setUp() {
//        TODO("Not yet implemented")
//    }

    @Test
    fun `fetching should not be triggered if it runs already`() {
        // Preparation
        var finishedFetching = false
        `when`(metricsFetchPortMock.fetchComponentMetrics())
            .thenAnswer {
                Thread.sleep(1000);
                finishedFetching = true

                null
            }
        thread {
            underTest.fetchData()
        }

        // Execution

        thread {
            underTest.fetchData()
        }

        // Assertion
        verify(metricsFetchPortMock, times(1)).fetchComponentMetrics()
        await.until { finishedFetching }
    }
}
