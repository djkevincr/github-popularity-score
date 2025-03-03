/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.github.popularity.model;

/**
 * Language Enum.
 *
 * @author Kevin Ratnasekera
 */
public enum Language {
  // added few languages for validation purposes
  JAVA("java"),
  JAVASCRIPT("javascript"),
  RUBY("ruby"),
  RUST("rust");

  String language;

  Language(String language) {
    this.language = language;
  }

  public static Language convert(String input) {
    if ("java".equals(input)) {
      return Language.JAVA;
    } else if ("javascript".equals(input)) {
      return Language.JAVASCRIPT;
    } else if ("ruby".equals(input)) {
      return Language.RUBY;
    } else if ("rust".equals(input)) {
      return Language.RUST;
    } else {
      return null;
    }
  }

}
