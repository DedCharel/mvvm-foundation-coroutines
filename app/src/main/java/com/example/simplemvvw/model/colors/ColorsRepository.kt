package com.example.simplemvvw.model.colors

import com.example.simplemvvw.model.Repository

typealias ColorListener = (NamedColor) -> Unit
/**
 * Repository interface example
 *
 * Provides access to the available and current selected color
 */
interface ColorsRepository:Repository {

    val currentColor: NamedColor

    /**
     * Get the list of all available colors that may be chosen by the user.
     */
    fun getAvailableColors(): List<NamedColor>

    /**
     * Get the color content by its ID
     */
    fun getById(id: Long): NamedColor

    /**
     * Listen for the current color changes.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addListener(listener: ColorListener)

    /**
     * Stop listening for the current color changes
     */
    fun removeListener(listener: ColorListener)
}