package com.buenaflor.kflogger

@Suppress("NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
public actual typealias KMetadataKey<T> = MetadataKey<T>

public actual val <T> KMetadataKey<T>.label: String
  get() = label

public actual val <T> KMetadataKey<T>.bloomFilterMask: Long
  get() = bloomFilterMask
