package com.giancarlobuenaflor.kflogger

import kotlin.reflect.KClass

public actual typealias Klass<T> = Class<T>

public actual fun <T : Any> KClass<T>.toKlass(): Klass<T> {
  return this.java
}
