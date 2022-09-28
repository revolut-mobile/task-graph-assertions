package com.revolut

internal class AssertionsContainer(
    private val path: String,
    private val allTasksPaths: Collection<String>,
    private val properties: Map<String, *>,
) {

    fun assertTasksTriggered(tasks: Set<String>) {
        val missingTasks = tasks.filter { !allTasksPaths.contains(it) }
        if (missingTasks.isNotEmpty()) {
            throw AssertionError(
                buildString {
                    appendLine("Task '$path' dependencies error:")
                    appendLine("Tasks are expected, but not triggered: $missingTasks")
                }
            )
        }
    }

    fun assertTasksNotTriggered(tasks: Set<String>) {
        val unexpectedTasks = tasks.filter { allTasksPaths.contains(it) }
        if (unexpectedTasks.isNotEmpty()) {
            throw AssertionError(
                buildString {
                    appendLine("Task '$path' dependencies error:")
                    appendLine("Tasks triggered, but should not: $unexpectedTasks")
                }
            )
        }
    }

    fun assertProjectProperties(map: Map<String, String>) {
        map.forEach { (key, value) -> assertProjectProperty(key, value) }
    }

    private fun assertProjectProperty(key: String, expectedValue: String) {
        if (!properties.containsKey(key)) {
            throw AssertionError("Required project property is missing: '$key'")
        } else {
            val actualValue = properties[key]
            if (expectedValue != actualValue) {
                throw AssertionError(
                    buildString {
                        appendLine("Expected project property value for key: '$key' differs from the actual one:")
                        appendLine("    Expected: '$expectedValue'")
                        appendLine("    Actual: '$actualValue'")
                    }
                )
            }
        }
    }
}
