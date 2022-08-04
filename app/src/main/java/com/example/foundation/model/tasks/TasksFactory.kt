package com.example.foundation.model.tasks

import com.example.foundation.model.Repository

typealias TaskBody<T> = () -> T

interface TasksFactory: Repository {

    fun <T> async(body: TaskBody<T>): Task<T>
}