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

import org.github.popularity.client.Client;
import org.github.popularity.client.Response;
import org.github.popularity.dto.GithubSearchResponseDTO;
import org.github.popularity.exception.InternalServerException;
import org.github.popularity.mapper.DataMapper;
import org.github.popularity.model.GithubRepo;
import org.github.popularity.repo.GithubRepository;
import org.github.popularity.scoring.WeightedScoringStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Github Data Service Implementation.
 *
 * @author Kevin Ratnasekera
 */
@Service
public class GithubRepoDataServiceImpl implements GithubRepoDataService {

  @Autowired
  private GithubRepository githubRepository;
  @Autowired
  private Client client;
  @Autowired
  private DataMapper dataMapper;

  private Logger logger = LoggerFactory.getLogger(GithubRepoDataServiceImpl.class);

  @Override
  public GithubSearchResponseDTO searchFromDatabase(String language,
                                                    LocalDate createdDate,
                                                    int offset,
                                                    int limit) {
    long count = githubRepository.countGithubRepos(language, createdDate.atStartOfDay().atOffset(ZoneOffset.UTC));
    List<GithubRepo> githubRepos;
    if (count > 0) {
      githubRepos = githubRepository.fetchGithubRepos(language, createdDate.atStartOfDay()
              .atOffset(ZoneOffset.UTC), limit, offset);
    } else {
      githubRepos = null;
    }
    return dataMapper.toGithubSearchResponseDTO(count, githubRepos);
  }

  @Override
  public GithubSearchResponseDTO searchFromAPI(String language,
                                               LocalDate createdDate,
                                               int offset,
                                               int limit) {
    try {
      Response httpResponse = client.sendSearchRequest(language, createdDate, offset, limit);
      if (httpResponse.getStatus() == 200) {
        return dataMapper.toGithubSearchResponseDTO(httpResponse.getBody(), new WeightedScoringStrategy());
      } else {
        // improve with meaningful error messages
        logger.error("Github server returned status code {}.", httpResponse.getStatus());
        throw new InternalServerException("Internal Server Error Occurred.");
      }
    } catch (IOException ex) {
      logger.error("Error occurred when invoking Github search endpoint.", ex);
      throw new InternalServerException("Internal Server Error Occurred.", ex);
    }
  }

}
