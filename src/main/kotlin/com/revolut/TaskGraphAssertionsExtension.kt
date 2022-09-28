package com.revolut

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property

public abstract class TaskGraphAssertionsExtension {

    internal abstract val whenTaskRequested: NamedDomainObjectContainer<TaskGraphAssertions>

    public abstract val silentMode: Property<Boolean>

    public fun whenTaskRequested(action: Action<NamedDomainObjectContainer<TaskGraphAssertions>>) {
        action.execute(whenTaskRequested)
    }
}
