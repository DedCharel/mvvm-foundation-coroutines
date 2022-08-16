package com.example.simplemvvw

import com.example.foundation.SingletonScopeDependencies
import com.example.foundation.model.coroutines.IoDispatcher
import com.example.foundation.model.coroutines.WorkerDispatcher
import com.example.simplemvvw.model.colors.InMemoryColorsRepository
import kotlinx.coroutines.Dispatchers

object Initializer {

    //Place your singleton scope dependencies here
    fun initDependencies() = SingletonScopeDependencies.init { applicationContext ->
            val ioDispatcher = IoDispatcher(Dispatchers.IO)
            val workerDispatcher = WorkerDispatcher(Dispatchers.Default)

            return@init listOf(
                ioDispatcher,
                workerDispatcher,
                InMemoryColorsRepository(ioDispatcher)
            )
    }
}

