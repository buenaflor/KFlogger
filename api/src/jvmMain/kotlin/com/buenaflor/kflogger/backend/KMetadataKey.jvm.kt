package com.buenaflor.kflogger.backend

import com.buenaflor.kflogger.MetadataKey

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
actual typealias KMetadataKey<T> = MetadataKey<T>

actual val <T> KMetadataKey<T>.label: String
  get() = label

actual val <T> KMetadataKey<T>.bloomFilterMask: Long
  get() = bloomFilterMask
