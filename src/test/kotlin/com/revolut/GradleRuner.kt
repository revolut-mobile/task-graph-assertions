package com.revolut

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

internal fun assertBuildSuccess(projectDir: File, vararg args: String): BuildResult {
    return createRunner(projectDir, args).build()
}

internal fun assertBuildFailed(projectDir: File, vararg args: String): BuildResult {
    return createRunner(projectDir, args).buildAndFail()
}

internal fun createRunner(projectDir: File, args: Array<out String>): GradleRunner {
    return GradleRunner.create()
        .withProjectDir(projectDir)
        .withPluginClasspath()
        .forwardOutput()
        .withDebug(true)
        .withArguments(listOf("--dry-run").plus(args))
}
