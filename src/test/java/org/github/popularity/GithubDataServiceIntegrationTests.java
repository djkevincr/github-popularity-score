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
package org.github.popularity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.github.popularity.controller.GithubRepoDataController;
import org.github.popularity.dto.GithubRepoDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class GithubDataServiceIntegrationTests {

  private static final String ASYNC_URL_TEMPLATE = "/api/v1/search/repositories?language={language}&createdDate={date}&offset={offset}&limit={limit}";
  private static final String SYNC_URL_TEMPLATE = "/api/v1/search/repositories/sync?language={language}&createdDate={date}&offset={offset}&limit={limit}";
  private static final String TEST_FILE = "test.json";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GithubRepoDataController githubRepoDataController;

  @Autowired
  private GithubRepository githubRepository;

  @Autowired
  private DataMapper dataMapper;

  @BeforeEach
  public void setup() {
    githubRepository.deleteAll();
  }

  @AfterEach
  public void destroy() {
    githubRepository.deleteAll();;
  }

  @Test
  public void contexLoads() {
    assertThat(githubRepoDataController).isNotNull();
  }

  @Test
  public void testAsyncAPI() throws Exception {
    Path filePath = Paths.get(getClass().getClassLoader().getResource(TEST_FILE).getPath());
    String content = new String(Files.readAllBytes(Paths.get(filePath.toUri())));
    List<GithubRepo> repos = dataMapper.toGithubRepo(content, new WeightedScoringStrategy());
    // save
    repos.forEach(repo -> {
      githubRepository.save(repo);
    });
    String dbFetchUrl = ASYNC_URL_TEMPLATE
            .replace("{language}", "java")
            .replace("{date}", "2025-01-01")
            .replace("{offset}", "0")
            .replace("{limit}", "20");

    MvcResult mvcResult = this.mockMvc.perform(get(dbFetchUrl))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.totalCount", is(20))).andReturn();
    String body = new String(mvcResult.getResponse().getContentAsByteArray());
    GithubSearchResponseDTO responseDTO = new ObjectMapper()
            .registerModule(new JavaTimeModule()).readValue(body, GithubSearchResponseDTO.class);
    Assert.assertEquals(responseDTO.getTotalCount(), new Long(20L));

    for (GithubRepoDTO item : responseDTO.getItems()) {
      // compare repo returned by api vs whats stored in db / test json file
      GithubRepo dbRepo = githubRepository.findByRepositoryId(item.getRepositoryId());
      Assert.assertEquals(item.getLanguage(), dbRepo.getLanguage());
      Assert.assertEquals(item.getRepositoryId(), dbRepo.getRepositoryId());
      Assert.assertEquals(item.getScore(), dbRepo.getScore());
      Assert.assertEquals(item.getUrl(), dbRepo.getUrl());
    }
  }

  @Test
  public void testAsyncAPIUnknownLanguage() throws Exception {
    Path filePath = Paths.get(getClass().getClassLoader().getResource(TEST_FILE).getPath());
    String content = new String(Files.readAllBytes(Paths.get(filePath.toUri())));
    List<GithubRepo> repos = dataMapper.toGithubRepo(content, new WeightedScoringStrategy());
    // save
    repos.forEach(repo -> {
      githubRepository.save(repo);
    });
    String  dbFetchUrl = ASYNC_URL_TEMPLATE
            .replace("{language}", "javac")
            .replace("{date}", "2025-01-01")
            .replace("{offset}", "0")
            .replace("{limit}", "10");

    this.mockMvc.perform(get(dbFetchUrl))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void testAsyncAPIPageLimitExceeds() throws Exception {
    Path filePath = Paths.get(getClass().getClassLoader().getResource(TEST_FILE).getPath());
    String content = new String(Files.readAllBytes(Paths.get(filePath.toUri())));
    List<GithubRepo> repos = dataMapper.toGithubRepo(content, new WeightedScoringStrategy());
    // save
    repos.forEach(repo -> {
      githubRepository.save(repo);
    });
    String  dbFetchUrl = ASYNC_URL_TEMPLATE
            .replace("{language}", "java")
            .replace("{date}", "2025-01-01")
            .replace("{offset}", "0")
            .replace("{limit}", "200");

    this.mockMvc.perform(get(dbFetchUrl))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void testSyncAPI() throws Exception {
    String  dbFetchUrl = SYNC_URL_TEMPLATE
            .replace("{language}", "java")
            .replace("{date}", "2025-01-01")
            .replace("{offset}", "0")
            .replace("{limit}", "10");

    this.mockMvc.perform(get(dbFetchUrl))
            .andExpect(status().is2xxSuccessful());
  }

  @Test
  public void testSyncAPIUnknownLanguage() throws Exception {
    String  dbFetchUrl = SYNC_URL_TEMPLATE
            .replace("{language}", "computer")
            .replace("{date}", "2025-01-01")
            .replace("{offset}", "0")
            .replace("{limit}", "10");
    this.mockMvc.perform(get(dbFetchUrl))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void testSyncAPIPageLimitExceeds() throws Exception {
    String  dbFetchUrl = SYNC_URL_TEMPLATE
            .replace("{language}", "computer")
            .replace("{date}", "2025-01-01")
            .replace("{offset}", "0")
            .replace("{limit}", "400");

    this.mockMvc.perform(get(dbFetchUrl))
            .andExpect(status().isBadRequest());
  }

}
