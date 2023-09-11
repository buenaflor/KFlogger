package com.giancarlobuenaflor.kflogger.context

import com.giancarlobuenaflor.kflogger.util.IgnoreIos
import kotlin.test.Test

class KTagsTest {
  @Test
  @IgnoreIos
  fun testNotCrashing() {
    // This test is to ensure that the code compiles and does not crash.
    val tags1 = KTags.empty()
    val tags2 = KTags.empty()
    tags1.asMap()
    tags1.isEmpty()
    tags1.merge(tags2)
    tags1.hashCode()
    tags1.equals(tags2)
    tags1.toString()
    KTags.builder()
    KTags.of("key", "value")
    KTags.of("key", true)
    KTags.of("key", 1L)
    KTags.of("key", 1.0)

    val tagsBuilder = KTagsBuilder()
    tagsBuilder.addTag("key")
    tagsBuilder.addTag("key", "value")
    tagsBuilder.addTag("key", 1L)
    tagsBuilder.addTag("key", 1.0)
    tagsBuilder.addTag("key", true)
    tagsBuilder.build()
  }
}
