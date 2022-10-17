package com.revolut

import org.gradle.TaskExecutionRequest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

internal const val TAG: String = "TaskGraphAssertionsPlugin"

public class TaskGraphAssertionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val extension = target.extensions.create<TaskGraphAssertionsExtension>("taskGraphAssertions")

        target.gradle.taskGraph.whenReady { graph ->
            val allTaskPaths: Set<String> = graph.allTasks.map { it.path }.toSet()

            val logger = Logger(
                logger = target.logger,
                logLevelProvider = extension.logLevel
            )

            extension.whenRequested.all { assertions ->
                val requestedTask = assertions.name

                if (!target.gradle.startParameter.taskRequests.isRequested(requestedTask)) {
                    logger.log("Task '$requestedTask' was not requested. Skip assertions")
                } else {
                    performAssertions(
                        requestedTask = requestedTask,
                        allTaskPaths = allTaskPaths,
                        properties = target.properties,
                        assertions = assertions
                    )
                    logger.log("Task '$requestedTask' was requested. All assertions are OK!")
                }
            }

            extension.whenExecuted.all { assertions ->
                val requestedTask = assertions.name

                if (!allTaskPaths.contains(requestedTask)) {
                    logger.log("Task '$requestedTask' was not executed. Skip assertions")
                } else {
                    performAssertions(
                        requestedTask = requestedTask,
                        allTaskPaths = allTaskPaths,
                        properties = target.properties,
                        assertions = assertions
                    )
                    logger.log("Task '$requestedTask' was executed. All assertions are OK!")
                }
            }
        }
    }

    private fun List<TaskExecutionRequest>.isRequested(taskName: String): Boolean {
        val args = flatMap { it.args }
        return args.contains(taskName)
    }

    private fun performAssertions(
        requestedTask: String,
        allTaskPaths: Set<String>,
        properties: Map<String, *>,
        assertions: TaskGraphAssertions,
    ) {
        val container = AssertionsContainer(
            path = requestedTask,
            allTasksPaths = allTaskPaths,
            properties = properties
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
    }
}
