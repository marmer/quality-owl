package io.github.marmer.tools.adapter.api

import io.github.marmer.tools.usecases.MetricsUpdater
import java.time.LocalDate
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Path("/diffs")
@Produces(MediaType.APPLICATION_JSON)
class DiffsController(private val metricsUpdater: MetricsUpdater) {
    @GET
    fun getDiffs(@QueryParam("limit") limit: Int, @QueryParam("since") startDate: LocalDate) {
        TODO("Not yet implemented")
    }

}
