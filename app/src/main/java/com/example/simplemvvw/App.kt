package com.example.simplemvvw

import android.app.Application
import com.example.foundation.BaseApplication
import com.example.foundation.model.coroutines.IoDispatcher
import com.example.foundation.model.coroutines.WorkerDispatcher
import com.example.simplemvvw.model.colors.InMemoryColorsRepository
import kotlinx.coroutines.Dispatchers

/**
* Here we store instances of model layer classes
*/
class App:Application(), BaseApplication {

    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val workerDispatcher = WorkerDispatcher(Dispatchers.Default)


    override val singletonScopeDependencies: List<Any> = listOf(
        InMemoryColorsRepository(ioDispatcher)
    )
}