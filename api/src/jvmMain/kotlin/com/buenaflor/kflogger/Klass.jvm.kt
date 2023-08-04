package com.buenaflor.kflogger

import kotlin.reflect.KClass

public actual typealias Klass<T> = Class<T>

public actual fun <T : Any> KClass<T>.toKlass(): Klass<T> {
    println(this.java.name)
    println(this.java.canonicalName)
    return this.java
}

