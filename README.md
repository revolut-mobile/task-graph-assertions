# Task Graph Assertions Gradle Plugin

Add assertions to be aware that your complex gradle build tasks hierarchy is still here.

## When to use it?

Most common scenario is to create a safety net of assertions for 
[Lifecycle tasks](https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:lifecycle_tasks) 
declared in [precompiled convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html#compiling_convention_plugins)

## How to use

1. Configure desired checks by applying plugin to project (root or whatever project/module you need to check)

    ```groovy
    plugins {
        id 'com.revolut.task-graph-assertions'
    }
    
    taskGraphAssertions {
   
        whenRequested {
            register(":check") {
                
                assertTasksTriggered.addAll(":test")
    
                assertTasksNotTriggered.addAll(":assemble")
    
                assertProjectProperties.put("customProperty", "true")
            }
        }
    
        whenExecuted {
            register(":build") {
                
                assertTasksTriggered.addAll(":check", ":assemble")
    
                assertTasksNotTriggered.addAll(":test")
    
                assertProjectProperties.put("customProperty", "true")
            }
        }
    }
    ```

2. Run check by executing requested task

    ```
    ./gradlew :build --dry-run
    ```

   Check build log for message: `Task ':build' was requested. All assertions are OK!`

## Requested vs Executed

Requested tasks are the ones that you manually write after `./gradlew <HERE>`.

Executed tasks are every task that is executed during th build. (Always including one that requested)

## CI usage

To setup regular checks on CI, please keep in mind that every requested task should be checked in separate build.  
Different tasks in single build will result in assertions interfering with each other.

## Log Level

By default, plugin logs messages visible with default `LIFECYCLE` level. You can change it with:

```groovy
import org.gradle.api.logging.LogLevel

taskGraphAssertions {
    logLevel.set(LogLevel.DEBUG)
}
```

## Troubleshooting

Q: Plugin keep saying `[TaskGraphAssertionsPlugin] Task 'myCustomTask' was not executed. Skip assertions`, but I am
requesting exactly this task by calling `./gradlew myCustomTask`.

A: Most likely project that has `com.revolut.task-graph-assertions` plugin applied doesn't have `myCustomTask`.
Gradle treats tasks without `:` prefix differently: it tries to find task with that name across all subprojects and runs it.

You can solve it in two ways:

- by specifying full path of expected task, like `:myfeature:myCustomTask`
- by registering a [lifecycle](https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:lifecycle_tasks) 
`:myCustomTask` on root project, that all `:<module>:myCustomTask` will depend on. 
