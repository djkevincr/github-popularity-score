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
package org.github.popularity.scoring;


import java.time.OffsetDateTime;

/**
 * Scoring Strategy Interface. Currently scoring algorithm is based on three attributes.
 * stargazers count, forks count and last pushed/updated date.
 *
 * @author Kevin Ratnasekera
 */
public interface ScoringStrategy {

  /**
   * Assign Popularity Score.
   *
   * @param stargazersCount
   * @param forksCount
   * @param lastUpdated
   *
   * @return popularity score
   */
  Double score(Long stargazersCount, Long forksCount, OffsetDateTime lastUpdated);

}
