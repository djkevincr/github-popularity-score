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
package org.github.popularity.service;

import org.github.popularity.dto.GithubSearchResponseDTO;
import org.github.popularity.mapper.DataMapper;
import org.github.popularity.model.GithubRepo;
import org.github.popularity.repo.GithubRepository;
import org.github.popularity.scoring.WeightedScoringStrategy;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
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
class GithubRepoServiceTests {

  @Autowired
  private GithubRepository githubRepository;
  @Autowired
  private DataMapper dataMapper;
  @Autowired
  private GithubRepoDataService githubRepoDataService;

  @BeforeEach
  public void setup() {
    githubRepository.deleteAll();;
  }

  @AfterEach
  public void destroy() {
    githubRepository.deleteAll();;
  }

  private static final String TEST_FILE = "test.json";

  @Test
  public void testGithubRepoServiceDBSearch() throws IOException {
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

    GithubSearchResponseDTO responseDTO = githubRepoDataService.searchFromDatabase("java",
            LocalDate.parse("2025-01-01"), 0, 20);
    Assert.assertEquals(responseDTO.getTotalCount(), new Long(20L));
    Assert.assertEquals(responseDTO.getItems().size(), 20);

    responseDTO.getItems().stream()
            .forEach(repoDTO -> {
              GithubRepo fileRepo = repos.stream()
                      .filter(repo -> repo.getRepositoryId().equals(repoDTO.getRepositoryId()))
                      .findFirst().get();
              // should be equal between test json file and service returned json / dto object
              Assert.assertEquals(repoDTO.getUrl(), fileRepo.getUrl());
              Assert.assertEquals(repoDTO.getScore(), fileRepo.getScore());
              Assert.assertEquals(repoDTO.getRepositoryId(), fileRepo.getRepositoryId());
              Assert.assertEquals(repoDTO.getLanguage(), fileRepo.getLanguage());
            });
  }

}
