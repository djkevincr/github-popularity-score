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

import org.github.popularity.mapper.DataMapper;
import org.github.popularity.model.GithubRepo;
import org.github.popularity.scoring.WeightedScoringStrategy;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class GithubRepoRepositoryTests {

  @Autowired
  private GithubRepository githubRepository;
  @Autowired
  private DataMapper dataMapper;

  @BeforeEach
  public void setup() {
    githubRepository.deleteAll();;
  }

  private static final String TEST_FILE = "test.json";

  @Test
  public void testGithubRepository() throws IOException {
    Path filePath = Paths.get(getClass().getClassLoader().getResource(TEST_FILE).getPath());
    String content = new String(Files.readAllBytes(Paths.get(filePath.toUri())));
    List<GithubRepo> repos = dataMapper.toGithubRepo(content, new WeightedScoringStrategy());
    // save
    repos.forEach(repo -> {
      githubRepository.save(repo);
    });
    Assert.assertEquals(githubRepository.count(), 20);
    long repoCount = githubRepository.countGithubRepos("java", LocalDate.parse("2025-01-01").atStartOfDay()
            .atOffset(ZoneOffset.UTC));
    Assert.assertEquals(repoCount, 20);

    // re parse test.json and see whether it matches with db
    List<GithubRepo> parsedRepos = dataMapper.toGithubRepo(content, new WeightedScoringStrategy());
    parsedRepos.stream().forEach(repo -> {
      GithubRepo dbRepo = githubRepository.findByRepositoryId(repo.getRepositoryId());
      // should be equal between db stored and json parsed entities
      Assert.assertEquals(dbRepo.getUrl(), repo.getUrl());
      Assert.assertEquals(dbRepo.getScore(), dbRepo.getScore());
      Assert.assertEquals(dbRepo.getRepositoryId(), dbRepo.getRepositoryId());
      Assert.assertEquals(dbRepo.getLanguage(), dbRepo.getLanguage());
      Assert.assertEquals(dbRepo.getStargazersCount(), dbRepo.getStargazersCount());
      Assert.assertEquals(dbRepo.getForksCount(), dbRepo.getForksCount());
    });

    List<GithubRepo> paginatedList = githubRepository.fetchGithubRepos("java", LocalDate.parse("2025-01-01").atStartOfDay()
            .atOffset(ZoneOffset.UTC), 10, 0);
    Assert.assertEquals(paginatedList.size(), 10);
  }

}
