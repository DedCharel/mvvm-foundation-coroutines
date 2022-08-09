package com.example.foundation.model.tasks.factories

import android.os.Handler
import android.os.HandlerThread
import com.example.foundation.model.tasks.AbstractTask
import com.example.foundation.model.tasks.SynchronizedTask
import com.example.foundation.model.tasks.Task
import com.example.foundation.model.tasks.TaskListener

class HandlerThreadTasksFactory: TasksFactory {

    private val thread = HandlerThread(javaClass.simpleName)

    init {
        thread.start()
    }

    private val handler = Handler(thread.looper)
    private var destroyed = false

    override fun <T> async(body: TaskBody<T>): Task<T> {
        if (destroyed) throw IllegalStateException("Factory is closed")
        return SynchronizedTask(HandlerThreadTask(body))
    }

    /**
     * Stop the [HandlerThread]. All further class of [async] will throw exception.
     */
    fun close() {
        destroyed = true
        thread.quitSafely()
    }

    private inner class HandlerThreadTask<T>(
        private val body: TaskBody<T>
    ) : AbstractTask<T>() {

        private var thread: Thread? = null

        override fun doEnqueue(listener: TaskListener<T>) {
            val runnable = Runnable {
                // using thread for cancelling tasks, because Handler.removeCallbacks
                // can't remove tasks which are already launched
                thread = Thread {
                    executeBody(body, listener)
                }
                thread?.start()
                // wait for thread finishing, otherwise more than 1 task body can be executed at a time
                thread?.join()
            }
            handler.post(runnable)
        }

        override fun doCancel() {
            thread?.interrupt()
        }

    }
}