/*
 * Copyright (C) 2021 The Flogger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.buenaflor.kflogger.util;

import static com.google.common.base.StandardSystemProperty.JAVA_SPECIFICATION_VERSION;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JavaLangAccessStackGetterTest {

  @Test
  public void testCallerOf() {
    assumeJava8();
    StackGetterTestUtil.runTestCallerOf(getJavaLangAccessStackGetter());
  }

  @Test
  public void testCallerOfBadOffset() {
    assumeJava8();
    StackGetterTestUtil.runTestCallerOfBadOffset(getJavaLangAccessStackGetter());
  }

  private static void assumeJava8() {
    assumeTrue(JAVA_SPECIFICATION_VERSION.value().equals("1.8"));
  }

  private static StackGetter getJavaLangAccessStackGetter() {
    try {
      return Class.forName("com.buenaflor.kflogger.util.JavaLangAccessStackGetter")
          .asSubclass(StackGetter.class)
          .getDeclaredConstructor()
          .newInstance();
    } catch (ReflectiveOperationException e) {
      throw new LinkageError(e.getMessage(), e);
    }
  }
}
