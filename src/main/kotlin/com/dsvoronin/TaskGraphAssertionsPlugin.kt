package com.dsvoronin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class TaskGraphAssertionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val extension = target.extensions.create<TaskGraphAssertionsExtension>("taskGraphAssertions")

        target.gradle.taskGraph.whenReady { graph ->
            val allTaskPaths: Set<String> = graph.allTasks.map { it.path }.toSet()

            val isInSilentMode = extension.silentMode.convention(false).get()

            extension.whenTaskRequested.all { assertions ->
                val requestedTask = assertions.name

                if (allTaskPaths.contains(requestedTask)) {

                    val container = AssertionsContainer(
                        path = requestedTask,
                        allTasksPaths = allTaskPaths,
                        properties = target.properties
                    )

                    assertions.assertTasksTriggered.orNull?.let {
                        container.assertTasksTriggered(it)
                    }
                    assertions.assertTasksNotTriggered.orNull?.let {
                        container.assertTasksNotTriggered(it)
                    }
                    assertions.assertProjectProperties.orNull?.let {
                        container.assertProjectProperties(it)
                    }

                    if (!isInSilentMode) {
                        target.logger.lifecycle("Task '$requestedTask' was requested. All assertions are OK!")
                    }
                }
            }
        }
    }
}
