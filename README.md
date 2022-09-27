# Task Graph Assertions Gradle Plugin

Add assertions to be aware that your complex gradle build tasks hierarchy is still here.

## How to use

1. Configure desired checks by applying plugin to project (root or whatever project/module you need to check)

    ```groovy
    plugins {
        id 'com.dsvoronin.task-graph-assertions'
    }
    
    taskGraphAssertions {
    
        whenTaskRequested {
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

## CI usage

To setup regular checks on CI, please keep in mind that every requested task should be checked in separate build.  
Different tasks in single build will result in assertions interfering with each other.
