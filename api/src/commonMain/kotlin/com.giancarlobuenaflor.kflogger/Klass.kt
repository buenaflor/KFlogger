package com.giancarlobuenaflor.kflogger

import kotlin.reflect.KClass

/**
 * The bridge class used to typealias `Class` for JVM and `KClass` for iOS. However, Class and
 * KClass have different signatures, so it is not possible to automatically typealias them with the
 * same class at once. The solution here is to save the `KClass` as a property of `Klass` in the iOS
 * implementation.
 *
 * Accessing the respective `Class` or `KClass` is done via the `toKlass()` extension function.
 */
public expect class Klass<T : Any>

/** Extension function to convert a `KClass` to a `Klass`. */
public expect fun <T : Any> KClass<T>.toKlass(): Klass<T>
