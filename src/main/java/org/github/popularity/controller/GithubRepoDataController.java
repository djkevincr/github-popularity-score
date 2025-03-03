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
package org.github.popularity.controller;

import org.github.popularity.client.Client;
import org.github.popularity.exception.BadRequestException;
import org.github.popularity.mapper.DataMapper;
import org.github.popularity.model.Language;
import org.github.popularity.repo.GithubRepository;
import org.github.popularity.service.GithubRepoDataService;
import org.github.popularity.worker.GithubDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.github.popularity.constant.GithubConstants.MAX_API_PAGE_SIZE;
import static org.github.popularity.constant.GithubConstants.SERVER_POOL_GRACEFUL_TERMINATION_DURATION;
import static org.github.popularity.constant.GithubConstants.SERVER_POOL_SIZE;

/**
 * Github Repository Data Controller.
 *
 * @author Kevin Ratnasekera
 */
@RestController
@RequestMapping(path = "/api/v1")
public class GithubRepoDataController {

  private Logger logger = LoggerFactory.getLogger(GithubRepoDataController.class);

  // components
  @Autowired
  GithubRepoDataService githubRepoDataService;
  @Autowired
  private GithubRepository githubRepository;
  @Autowired
  private Client client;
  @Autowired
  private DataMapper dataMapper;

  // env variables
  @Value("${github.search.language}")
  private String githubSearchLanguage;
  @Value("${github.search.created.date}")
  private String githubSearchCreatedDate;
  @Value("${github.data.fetch.enabled}")
  private Boolean githubDataFetchEnabled;

  // thread pool to execute background data fetch
  private ExecutorService serverThreadPool;

  @PostConstruct
  public void init() {
    this.serverThreadPool = Executors.newFixedThreadPool(SERVER_POOL_SIZE);
    if (Objects.nonNull(DateTimeFormatter.ISO_LOCAL_DATE.parse(githubSearchCreatedDate)) &&
            Objects.nonNull(Language.convert(githubSearchLanguage)) && githubDataFetchEnabled) {
      GithubDataProcessor githubDataProcessor = new GithubDataProcessor(githubRepository, client, dataMapper,
              githubSearchLanguage, githubSearchCreatedDate);
      this.serverThreadPool.submit(githubDataProcessor);
    }
  }

  @PreDestroy
  public void cleanUp() {
    try {
      this.serverThreadPool.awaitTermination(SERVER_POOL_GRACEFUL_TERMINATION_DURATION, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.error("Waiting on termination interrupted for thread pool serverThreadPool.", e);
    }
  }

  @GetMapping(path = "/search/repositories", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity searchRepositoriesDatabase(@RequestParam("language") String language,
                                                   @RequestParam("createdDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
                                                   @RequestParam("offset") int offset,
                                                   @RequestParam("limit") int limit) {
    logger.info("Client request received for async endpoint. language: {} created date: {} offset: {} limit: {}", language, createdDate, offset, limit);
    validateParameters(language, limit);
    return ResponseEntity.ok(githubRepoDataService.searchFromDatabase(language, createdDate, offset, limit));
  }

  @GetMapping(path = "/search/repositories/sync", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity searchRepositoriesAPI(@RequestParam("language") String language,
                                              @RequestParam("createdDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
                                              @RequestParam("offset") int offset,
                                              @RequestParam("limit") int limit) {
    logger.info("Client request received for sync endpoint. language: {} created date:  {} offset: {} limit: {}", language, createdDate, offset, limit);
    validateParameters(language, limit);
    return ResponseEntity.ok(githubRepoDataService.searchFromAPI(language, createdDate, offset, limit));
  }

  private void validateParameters(String language,
                                  int limit) {
    if (Objects.isNull(Language.convert(language)) || limit > MAX_API_PAGE_SIZE) {
      // improve error message so that client aware what went wrong.
      throw new BadRequestException("Request validations failed.");
    }
  }

}
