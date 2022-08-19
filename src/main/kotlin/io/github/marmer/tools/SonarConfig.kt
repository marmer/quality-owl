package io.github.marmer.tools

import io.smallrye.config.ConfigMapping
import java.util.*
import javax.validation.constraints.NotBlank

@ConfigMapping(prefix = "sonar")
interface SonarConfig {
    @NotBlank
    fun url(): String

    fun usernameOrToken(): Optional<String>

    fun password(): Optional<String>

    @NotBlank
    fun fetchIntervalCron(): String
}


