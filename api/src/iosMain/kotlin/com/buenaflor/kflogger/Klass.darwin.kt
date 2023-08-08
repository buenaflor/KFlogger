package com.buenaflor.kflogger

import kotlin.reflect.KClass

public actual class Klass<T : Any>(public val kClass: KClass<T>)

public actual fun <T : Any> KClass<T>.toKlass(): Klass<T> {
  return Klass(this)
}
