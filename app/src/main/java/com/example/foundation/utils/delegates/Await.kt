package com.example.foundation.utils.delegates

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class Await<T> {

    private val countDownLath = CountDownLatch(1)
    private var value = AtomicReference<T>(null)

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        countDownLath.await()
        return value.get()
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (value == null) return
        if (this.value.compareAndSet(null, value)) {
            countDownLath.countDown()
        }
    }
}