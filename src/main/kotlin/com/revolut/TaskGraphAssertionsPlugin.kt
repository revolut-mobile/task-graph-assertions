package com.revolut

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class TaskGraphAssertionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val extension = target.extensions.create<TaskGraphAssertionsExtension>("taskGraphAssertions")

        target.gradle.taskGraph.whenReady { graph ->
            val allTaskPaths: Set<String> = graph.allTasks.map { it.path }.toSet()

            val logger = Logger(
                logger = target.logger,
                logLevelProvider = extension.logLevel
            )

            extension.whenExecuted.all { assertions ->
                val requestedTask = assertions.name

                if (!allTaskPaths.contains(requestedTask)) {
                    logger.log("Task '$requestedTask' was not executed. Skip assertions")
                } else {

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

                    logger.log("Task '$requestedTask' was executed. All assertions are OK!")
                }
            }
        }
    }
}
