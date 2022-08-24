package io.github.marmer.tools

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response


@QuarkusTest
@TestProfile(ExampleResourceTest.WithAuthAndProjectIncludes::class)
class ExampleResourceTest {


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
    fun testCallToSonar() {
        clientAndServer
            .`when`(
                request()
                    .withMethod("GET")
                    .withPath("/blub/measures/component")
                    .withHeader("Authentication", "Basic dW46cHc=")
            )
            .respond(
                response()
                    .withBody("{\n  \"component\": {\n    \"key\": \"fancy-beaver.in:da-house\",\n    \"name\": \"fbidh\",\n    \"description\": \"Big bouncing beaver belly\",\n    \"qualifier\": \"TRK\",\n    \"measures\": [\n      {\n        \"metric\": \"code_smells\",\n        \"value\": \"534\",\n        \"bestValue\": false\n      },\n      {\n        \"metric\": \"bugs\",\n        \"value\": \"3\",\n        \"bestValue\": false\n      }\n    ]\n  }\n}")
            )
    }

    @Test
    fun testHelloEndpoint() {
        given()
            .`when`().get("/hello")
            .then()
            .statusCode(200)
            .body(`is`("Hello from RESTEasy Reactive"))
    }

    class WithAuthAndProjectIncludes : QuarkusTestProfile {
        override fun getConfigOverrides(): Map<String, String> =
            mapOf(
                Pair("sonar.url", "https://localhost:${clientAndServer.port}/blub"),
                Pair("sonar.username-or-token", "un"),
                Pair("sonar.password", "pw"),
                Pair("sonar.project-includes", "fancy-beaver.in:da-house"),
                Pair("sonar.metric-keys", "bugs,code_smells"),
            )
    }
}
