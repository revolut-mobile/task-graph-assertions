package com.revolut

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Property

internal class Logger(
    private val logger: Logger,
    logLevelProvider: Property<LogLevel>
) {

    private val tag = "TaskGraphAssertionsPlugin"

    private val defaultLogLevel = LogLevel.LIFECYCLE

    private val logLevel = logLevelProvider.convention(defaultLogLevel).get()

    fun log(message: String) {
        logger.log(logLevel, "[$tag] $message")
    }
}
