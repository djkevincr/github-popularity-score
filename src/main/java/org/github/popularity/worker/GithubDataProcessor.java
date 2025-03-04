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
package org.github.popularity.worker;

import org.github.popularity.client.Client;
import org.github.popularity.client.Response;
import org.github.popularity.mapper.DataMapper;
import org.github.popularity.model.GithubRepo;
import org.github.popularity.repo.GithubRepository;
import org.github.popularity.scoring.WeightedScoringStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.github.popularity.constant.GithubConstants.GITHUB_SEARCH_PAGE_SIZE;

/**
 * Background runner task to fetch data from Github search API endpoint.
 *
 * @author Kevin Ratnasekera
 */
public class GithubDataProcessor implements Runnable {

  private GithubRepository githubRepository;
  private Client client;
  private DataMapper dataMapper;
  private String githubSearchLanguage;
  private String githubSearchCreatedDate;
  private Logger logger = LoggerFactory.getLogger(GithubDataProcessor.class);

  public GithubDataProcessor(GithubRepository githubRepository,
                             Client client,
                             DataMapper dataMapper,
                             String githubSearchLanguage,
                             String githubSearchCreatedDate) {
    this.githubRepository = githubRepository;
    this.client = client;
    this.dataMapper = dataMapper;
    this.githubSearchLanguage = githubSearchLanguage;
    this.githubSearchCreatedDate = githubSearchCreatedDate;
  }

  @Override
  public void run() {
    try {
      Response response = client.sendSearchRequest(githubSearchLanguage, LocalDate.parse(githubSearchCreatedDate), 0, 1);
      if (response.getStatus() == 200) {
        Long totalCount = dataMapper.toTotalCount(response.getBody());
        Long totalPages = totalCount / GITHUB_SEARCH_PAGE_SIZE;
        for (int currentPage = 0; currentPage < totalPages; currentPage++) {
          Response searchResponse = client.sendSearchRequest(githubSearchLanguage, LocalDate.parse(githubSearchCreatedDate), currentPage, GITHUB_SEARCH_PAGE_SIZE);
          if (searchResponse.getStatus() == 200) {
            List<GithubRepo> repoList = dataMapper.toGithubRepo(searchResponse.getBody(), new WeightedScoringStrategy());
            repoList.forEach(repo -> {
              GithubRepo dbRepo = githubRepository.findByRepositoryId(repo.getRepositoryId());
              if (Objects.isNull(dbRepo)) {
                githubRepository.save(repo);
              } else {
                dbRepo.setScore(repo.getScore());
                dbRepo.setScoredDate(repo.getScoredDate());
                githubRepository.save(dbRepo);
              }
              logger.info("Github repository {} url {} score {} stored in database.", repo.getRepositoryId(), repo.getUrl(), repo.getScore());
            });
          } else if (searchResponse.getStatus() == 403) {
            logger.info("Rate limit exceeded in Github search endpoint.");
            // exit the thread execution here.
            // as a further improvement, should improve logic here by considering rate limit http response headers
            // x-ratelimit-remaining, x-ratelimit-used and x-ratelimit-reset
            break;
          } else {
            // interpret what went wrong here
            logger.error("Github server returned status code {}.", searchResponse.getStatus());
          }
        }
      } else {
        // exit the thread execution
      }
    } catch (IOException ex) {
      logger.error("Exception occurred when calling Github Search endpoint.", ex);
    }
  }

}
