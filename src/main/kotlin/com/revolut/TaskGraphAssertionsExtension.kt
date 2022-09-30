package com.revolut

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property

public abstract class TaskGraphAssertionsExtension {

    internal abstract val whenExecuted: NamedDomainObjectContainer<TaskGraphAssertions>

    public abstract val logLevel: Property<LogLevel>

    public fun whenExecuted(action: Action<NamedDomainObjectContainer<TaskGraphAssertions>>) {
        action.execute(whenExecuted)
    }
}
