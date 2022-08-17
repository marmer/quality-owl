package io.github.marmer.tools

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam


@Path("/")
@RegisterRestClient(configKey = "sonar")
interface SonarClient {
    @GET
    @Path("/measures/component")
    fun getCoverage(
        @QueryParam("component") component: String,
        @QueryParam("metricKeys") metricKeys: String
    ): String;
}
