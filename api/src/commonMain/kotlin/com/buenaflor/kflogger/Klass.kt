package com.buenaflor.kflogger

import kotlin.reflect.KClass

public expect class Klass<T : Any>

public expect fun <T : Any> KClass<T>.toKlass(): Klass<T>
