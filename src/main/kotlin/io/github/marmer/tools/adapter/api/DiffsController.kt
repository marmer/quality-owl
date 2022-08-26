package io.github.marmer.tools.adapter.api

import java.time.LocalDate
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Path("/diffs")
@Produces(MediaType.APPLICATION_JSON)
class DiffsController {
    @GET
    fun getDiffs(@QueryParam("limit") limit: Int, @QueryParam("since") startDate: LocalDate) =
        "Wooohooo ${limit} ${startDate}"
}
