package com.buenaflor.kflogger

import kotlin.reflect.KClass

public actual class Klass<T : Any>(public val kClass: KClass<T>)
