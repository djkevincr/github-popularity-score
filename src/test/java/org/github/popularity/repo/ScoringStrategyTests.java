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
package org.github.popularity.repo;

import org.github.popularity.scoring.ScoringStrategy;
import org.github.popularity.scoring.WeightedScoringStrategy;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ScoringStrategyTests {

  @Test
  public void scoringStrategyAlgorithm() {
    // total score of 100
    ScoringStrategy scoringStrategy = new WeightedScoringStrategy();
    Double score = scoringStrategy.score(150000L, 20000L, OffsetDateTime.now(ZoneOffset.UTC));
    Assert.assertEquals(score, Double.valueOf(100));

    // 3 points each stars and fork count
    score = scoringStrategy.score(20000L, 600L, OffsetDateTime.now(ZoneOffset.UTC).minusDays(30));
    Assert.assertEquals(score, Double.valueOf(72.22222222222221));

    // 2 points each stars and fork count
    score = scoringStrategy.score(200L, 200L, OffsetDateTime.now(ZoneOffset.UTC).minusDays(100));
    Assert.assertEquals(score, Double.valueOf(44.44444444444444));

    // 1 points each stars and fork count
    score = scoringStrategy.score(50L, 50L, OffsetDateTime.now(ZoneOffset.UTC).minusDays(100));
    Assert.assertEquals(score, Double.valueOf(22.22222222222222));

    // 1 and 0 points each stars and fork count
    score = scoringStrategy.score(50L, 0L, OffsetDateTime.now(ZoneOffset.UTC).minusDays(100));
    Assert.assertEquals(score, Double.valueOf(11.11111111111111));

    //total score of 0
    score = scoringStrategy.score(0L, 0L, OffsetDateTime.now(ZoneOffset.UTC).minusDays(100));
    Assert.assertEquals(score, Double.valueOf(0));
  }

}
