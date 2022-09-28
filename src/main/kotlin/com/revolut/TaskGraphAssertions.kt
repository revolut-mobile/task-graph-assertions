package com.revolut

import org.gradle.api.Named
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.SetProperty

public abstract class TaskGraphAssertions(private val name: String) : Named {

    public abstract val assertTasksTriggered: SetProperty<String>

    public abstract val assertTasksNotTriggered: SetProperty<String>

    public abstract val assertProjectProperties: MapProperty<String, String>

    override fun getName(): String = name
}
