package com.example.simplemvvw

import android.app.Application
import com.example.foundation.BaseApplication
import com.example.foundation.model.tasks.ThreadUtils
import com.example.foundation.model.tasks.dispatchers.MainThreadDispatcher
import com.example.foundation.model.tasks.factories.ExecutorServiceTasksFactory
import com.example.foundation.model.tasks.factories.HandlerThreadTasksFactory
import com.example.foundation.model.tasks.factories.ThreadTasksFactory
import com.example.simplemvvw.model.colors.InMemoryColorsRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
* Here we store instances of model layer classes
*/
class App:Application(), BaseApplication {

    private val taskFactory = ThreadTasksFactory()
    // private val taskFactory = ExecutorServiceTasksFactory(Executors.newCachedThreadPool())
    // private val taskFactory = HandlerThreadTasksFactory()
    private val  threadUtils = ThreadUtils.Default()

    private val dispatcher = MainThreadDispatcher()

    override val singletonScopeDependencies: List<Any> = listOf(
        taskFactory,
        dispatcher,
        InMemoryColorsRepository(taskFactory, threadUtils)
    )
}