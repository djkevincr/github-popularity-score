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
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Weighted average Scoring Strategy Interface Implementation.
 *
 * @author Kevin Ratnasekera
 */
public class WeightedScoringStrategy implements ScoringStrategy {

  private static final int STARGAZERS_COUNT_WEIGHT = 4;
  private static final int FORK_COUNT_WEIGHT = 4;
  private static final int LAST_UPDATED_WEIGHT = 2;

  /**
   * Algorithm is based on weighted average score. This scoring algorithm is implemented simple and straight forward.
   *
   * Stargazers Count - Directly proportional / relationship to popularity score.
   * Fork Count - Directly proportional / relationship to popularity score.
   * Last updated/pushed date - Inversely proportional / relationship to popularity score.
   *
   * We give Stargazers Count and Fork Count an equal weight of 4 and Last updated date a weight of 2.
   * This is assuming Stargazers and Fork Count is a better indicator of popularity of Github Repo compared to updated date.
   * For example Last pushed date is much recent for newly created repo.
   *
   * This is how the points will be given for each attribute level.
   *
   * stargazersCount >= 100000 - 4 points
   * stargazersCount < 100000 && stargazersCount >= 1000 - 3 points
   * stargazersCount < 1000 && stargazersCount >= 100 - 2 points
   * stargazersCount < 100 && stargazersCount > 0 - 1 point
   *
   * forksCount >= 1000 - 4 points
   * forksCount < 1000 && forksCount >= 500 - 3 points
   * forksCount < 500 && forksCount >= 100 - 2 points
   * forksCount < 100 && forksCount > 0 - 1 point
   *
   * daysSinceLastUpdate <= 28 - 2 points
   * daysSinceLastUpdate > 28 && daysSinceLastUpdate <= 56 - 1 point
   *
   * each attribute level points are multiplied by respective weight and normalized by totals weight.
   * Then we multiply such normalized score by 100, so that our popularity score is between 0 and 100.
   */
  public Double score(Long stargazersCount,
                      Long forksCount,
                      OffsetDateTime lastUpdated) {
    Double score = 0.0;

    // stargazers score
    if (stargazersCount >= 100000) {
      score = score + STARGAZERS_COUNT_WEIGHT * 4;
    } else if (stargazersCount < 100000 && stargazersCount >= 1000) {
      score = score + STARGAZERS_COUNT_WEIGHT * 3;
    } else if (stargazersCount < 1000 && stargazersCount >= 100) {
      score = score + STARGAZERS_COUNT_WEIGHT * 2;
    } else if (stargazersCount < 100 && stargazersCount > 0) {
      score = score + STARGAZERS_COUNT_WEIGHT * 1;
    }

    // forks score
    if (forksCount >= 1000) {
      score = score + FORK_COUNT_WEIGHT * 4;
    } else if (forksCount < 1000 && forksCount >= 500) {
      score = score + FORK_COUNT_WEIGHT * 3;
    } else if (forksCount < 500 && forksCount >= 100) {
      score = score + FORK_COUNT_WEIGHT * 2;
    } else if (forksCount < 100 && forksCount > 0) {
      score = score + FORK_COUNT_WEIGHT * 1;
    }

    // last updated score
    long daysSinceLastUpdate = ChronoUnit.DAYS.between(lastUpdated, OffsetDateTime.now(ZoneOffset.UTC));
    if (daysSinceLastUpdate <= 28) {
      score = score + LAST_UPDATED_WEIGHT * 2;
    } else if (daysSinceLastUpdate > 28 && daysSinceLastUpdate <= 56) {
      score = score + LAST_UPDATED_WEIGHT * 1;
    }

    long totalWeight = 4 * STARGAZERS_COUNT_WEIGHT + 4 * FORK_COUNT_WEIGHT + 2 * LAST_UPDATED_WEIGHT;
    return score / totalWeight * 100;
  }

}
