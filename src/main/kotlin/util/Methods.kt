package util

import kotlin.coroutines.*

external fun require(module: String): dynamic

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}

fun launch(block: suspend () -> Unit) {
    block.startCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resumeWith(result: Result<Unit>) = Unit
    })
}

private external fun setTimeout(function: () -> Unit, delay: Long)
suspend fun delay(ms: Long): Unit = suspendCoroutine { continuation -> setTimeout({ continuation.resume(Unit) }, ms) }

