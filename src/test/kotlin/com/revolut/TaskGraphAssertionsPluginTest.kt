package com.revolut

import com.google.common.truth.Truth
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TaskGraphAssertionsPluginTest {

    @Test
    fun `required task triggered - build success`(@TempDir projectDir: File) {
        createProject(
            projectDir, """
            |register(":build") {
            |    assertTasksTriggered.addAll(":check", ":assemble")
            |}
            |""".trimMargin()
        )
        val result = assertBuildSuccess(projectDir, ":build")
        Truth.assertThat(result.output).contains("Task ':build' was requested. All assertions are OK!")
    }

    @Test
    fun `required task not triggered - build failed`(@TempDir projectDir: File) {
        createProject(
            projectDir, """
            |register(":build") {
            |   assertTasksTriggered.addAll(":customTask")
            |}
            |""".trimMargin()
        )
        val result = assertBuildFailed(projectDir, ":build")
        Truth.assertThat(result.output).contains("Tasks are expected, but not triggered: [:customTask]")
    }

    @Test
    fun `unexpected task triggered - build failed`(@TempDir projectDir: File) {
        createProject(
            projectDir, """
            |register(":build") {
            |    assertTasksNotTriggered.addAll(":check")
            |}
            |""".trimMargin()
        )
        val result = assertBuildFailed(projectDir, ":build")
        Truth.assertThat(result.output).contains("Tasks triggered, but should not: [:check]")
    }

    @Test
    fun `unexpected task not triggered - build success`(@TempDir projectDir: File) {
        createProject(
            projectDir, """
            |register(":build") {
            |    assertTasksNotTriggered.addAll(":custom")
            |}
            |""".trimMargin()
        )
        val result = assertBuildSuccess(projectDir, ":build")
        Truth.assertThat(result.output).contains("Task ':build' was requested. All assertions are OK!")
    }

    @Test
    fun `required property is set - build success`(@TempDir projectDir: File) {
        createProject(
            projectDir, """
            |register(":build") {
            |    assertProjectProperties.put("customProperty", "true")
            |}
            |""".trimMargin()
        )
        val result = assertBuildSuccess(projectDir, ":build", "-PcustomProperty=true")
        Truth.assertThat(result.output).contains("Task ':build' was requested. All assertions are OK!")
    }

    @Test
    fun `required property is not set - build failed`(@TempDir projectDir: File) {
        createProject(
            projectDir, """
            |register(":build") {
            |    assertProjectProperties.put("customProperty", "true")
            |}
            |""".trimMargin()
        )
        val result = assertBuildFailed(projectDir, ":build")
        Truth.assertThat(result.output).contains("Required project property is missing: 'customProperty'")
    }

    @Test
    fun `required property has unexpected value - build failed`(@TempDir projectDir: File) {
        createProject(
            projectDir, """
            |register(":build") {
            |    assertProjectProperties.put("customProperty", "true")
            |}
            |""".trimMargin()
        )
        val result = assertBuildFailed(projectDir, ":build", "-PcustomProperty=false")
        Truth.assertThat(result.output)
            .contains(
                "Expected project property value for key: 'customProperty' differs from the actual one:\n" +
                    "      Expected: 'true'\n" +
                    "      Actual: 'false'"
            )
    }

    private fun createProject(projectDir: File, pluginConfig: String) {
        projectDir.file("settings.gradle", "rootProject.name = 'test-project'")
        projectDir.file(
            "build.gradle", """
            |plugins {
            |   id 'base'
            |   id 'com.revolut.task-graph-assertions'
            |}
            |
            |taskGraphAssertions {
            |   silentMode.set(false)
            |   
            |   whenTaskRequested {
            |       $pluginConfig
            |   }
            |}
            |""".trimMargin()
        )
    }

    private fun assertBuildSuccess(projectDir: File, vararg args: String): BuildResult {
        return createRunner(projectDir, args).build()
    }

    private fun assertBuildFailed(projectDir: File, vararg args: String): BuildResult {
        return createRunner(projectDir, args).buildAndFail()
    }

    private fun createRunner(projectDir: File, args: Array<out String>): GradleRunner {
        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .forwardOutput()
            .withArguments(listOf("--dry-run").plus(args))
    }
}
