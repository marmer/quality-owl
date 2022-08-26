package io.github.marmer.tools.configuration

import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.*
import javax.inject.Singleton


@Singleton
class SonarConfig constructor(
    @ConfigProperty(name = "sonar.url")
    val url: String,

    @ConfigProperty(name = "sonar.username-or-token")
    usernameOrToken: Optional<String>,

    @ConfigProperty(name = "sonar.password")
    password: Optional<String>,

    @ConfigProperty(name = "sonar.project-includes")
    projectIncludes: Optional<List<String>>,

    @ConfigProperty(name = "sonar.metric-keys")
    val metricKeys: List<String>,

    @ConfigProperty(name = "sonar.fetch-interval-cron")
    val fetchIntervalCronString: String
) {
    val usernameOrToken: String? = usernameOrToken.orElse(null)
    val password: String? = password.orElse(null)
    val projectIncludes: List<String> = projectIncludes.orElseGet { emptyList() }
}
