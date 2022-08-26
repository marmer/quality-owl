package io.github.marmer.tools.adapter.sonar

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Path("/")
@RegisterRestClient(configKey = "sonar")
interface SonarClient {

    @GET
    @Path("/measures/component")
    fun getMetrics(
        @QueryParam("component") component: String,
        @QueryParam("metricKeys") metricKeys: String
    ): KomponentMetricsResponseDTO


    class KomponentMetricsResponseDTO {
        lateinit var component: Component

        class Component {
            lateinit var key: String
            lateinit var measures: List<Measure>
            lateinit var name: String

            class Measure {
                var bestValue: Boolean = false
                lateinit var metric: String
                lateinit var value: String
                override fun toString(): String {
                    return "Measure(bestValue=$bestValue, metric='$metric', value='$value')"
                }
            }

            override fun toString(): String {
                return "Component(key='$key', measures=$measures, name='$name')"
            }
        }

        override fun toString(): String {
            return "KomponentMetricsResponseDTO(component=$component)"
        }
    }

    @GET
    @Path("/components/search")
    fun getProjects(
        @QueryParam("qualifiers") qualifiers: List<String>,
        @QueryParam("ps") pageSize: Int
    ): ProjectListResponseDTO

    class ProjectListResponseDTO {
        var components: List<Component> = emptyList()

        class Component {
            lateinit var key: String
            lateinit var name: String
            lateinit var project: String
            override fun toString(): String {
                return "Component(key='$key', name='$name', project='$project')"
            }
        }

        override fun toString(): String {
            return "ProjectListResponseDTO(components=$components)"
        }
    }
}
