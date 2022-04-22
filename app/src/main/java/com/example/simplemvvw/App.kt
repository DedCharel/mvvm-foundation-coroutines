package com.example.simplemvvw

import android.app.Application
import com.example.simplemvvw.model.colors.InMemoryColorsRepository

/**
* Here we store instances of model layer classes
*/
class App:Application() {

    /**
     * Place your repositories here, now we have only one repository
     */
    val models = listOf<Any>(
        InMemoryColorsRepository()
    )
}