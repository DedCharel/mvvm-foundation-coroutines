package com.example.simplemvvw.utils

import androidx.lifecycle.LiveData

/**
 * Represents "side effect".
 * Used in [LiveData] as a wrapper for events.
 * Каждый елемент в event будет обрабатываться один раз (чтобы LiveData не отдавала уже обработанный элемент)
 */
class Event<T>(
    private val value: T
) {
    private var handled: Boolean = false

    fun getValue(): T? {
        if (handled) return null
        handled = true
        return value
    }

}