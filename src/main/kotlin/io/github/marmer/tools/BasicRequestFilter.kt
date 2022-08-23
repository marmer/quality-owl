package io.github.marmer.tools

import java.util.*
import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.client.ClientRequestFilter
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.ext.Provider

@Priority(Priorities.AUTHENTICATION)
@Provider
class BasicRequestFilter(private val sonarConfig: SonarConfig) : ClientRequestFilter {
    override fun filter(requestContext: ClientRequestContext) {
        if (sonarConfig.usernameOrToken?.isNotBlank() == true && requestContext.uri.toString()
                .startsWith(sonarConfig.url)
        )
            requestContext.headers.add(HttpHeaders.AUTHORIZATION, sonarAuthHeaderValue)
    }

    private val sonarAuthHeaderValue: String =
        "Basic " + Base64.getEncoder()
            .encodeToString(("${sonarConfig.usernameOrToken}:${sonarConfig.password ?: ""}").toByteArray())
}
