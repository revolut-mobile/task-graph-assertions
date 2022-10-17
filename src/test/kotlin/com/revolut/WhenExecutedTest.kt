package com.revolut

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class WhenExecutedTest {

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
        Truth.assertThat(result.output).contains("Task ':build' was executed. All assertions are OK!")
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
        Truth.assertThat(result.output).contains("Task ':build' was executed. All assertions are OK!")
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
        Truth.assertThat(result.output).contains("Task ':build' was executed. All assertions are OK!")
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
        Truth.assertThat(result.output).contains("[$TAG] Required project property is missing: 'customProperty'")
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
            |import org.gradle.api.logging.LogLevel
            |
            |plugins {
            |   id 'base'
            |   id 'com.revolut.task-graph-assertions'
            |}
            |
            |taskGraphAssertions {
            |   logLevel.set(LogLevel.LIFECYCLE)
            |   
            |   whenExecuted {
            |       $pluginConfig
            |   }
            |}
            |""".trimMargin()
        )
    }
}
