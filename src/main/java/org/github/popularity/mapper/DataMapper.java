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

package org.github.popularity.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.popularity.dto.GithubRepoDTO;
import org.github.popularity.dto.GithubSearchResponseDTO;
import org.github.popularity.model.GithubRepo;
import org.github.popularity.scoring.ScoringStrategy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Data mapper component to map DTO to Domain and Domain to DTO vice versa.
 *
 * @author Kevin Ratnasekera
 */
@Component
public class DataMapper {

  public GithubRepoDTO toGithubRepoDTO(GithubRepo githubRepo) {
    return getGithubRepoDTO(githubRepo);
  }

  public List<GithubRepo> toGithubRepo(String searchResponse,
                                       ScoringStrategy scoringStrategy) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(searchResponse);
    JsonNode itemsNode = root.get("items");
    OffsetDateTime scoredTime = OffsetDateTime.now(ZoneOffset.UTC);
    List<GithubRepo> items = new ArrayList<>();
    if (itemsNode.isArray()) {
      for (JsonNode node : itemsNode) {
        GithubRepo githubRepo = new GithubRepo();
        githubRepo.setLanguage(node.get("language").asText().toLowerCase(Locale.ROOT));
        githubRepo.setCreatedDate(OffsetDateTime.parse(node.get("created_at").asText()));
        githubRepo.setRepositoryId(node.get("id").asLong());
        githubRepo.setUrl(node.get("clone_url").asText());
        githubRepo.setStargazersCount(node.get("stargazers_count").asLong());
        githubRepo.setForksCount(node.get("forks_count").asLong());
        githubRepo.setUpdatedDate(OffsetDateTime.parse(node.get("pushed_at").asText()));
        githubRepo.setScore(scoringStrategy.score(githubRepo.getStargazersCount(),
                githubRepo.getForksCount(), githubRepo.getUpdatedDate()));
        githubRepo.setScoredDate(scoredTime);
        items.add(githubRepo);
      }
    }
    return items;
  }

  public GithubSearchResponseDTO toGithubSearchResponseDTO(String searchResponse,
                                                           ScoringStrategy scoringStrategy) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(searchResponse);
    JsonNode itemsNode = root.get("items");
    List<GithubRepoDTO> items = new ArrayList<>();
    if (itemsNode.isArray()) {
      for (JsonNode node : itemsNode) {
        GithubRepoDTO githubRepoDTO = new GithubRepoDTO();
        githubRepoDTO.setLanguage(node.get("language").asText().toLowerCase(Locale.ROOT));
        githubRepoDTO.setCreatedDate(OffsetDateTime.parse(node.get("created_at").asText()));
        githubRepoDTO.setRepositoryId(node.get("id").asLong());
        githubRepoDTO.setUrl(node.get("clone_url").asText());
        githubRepoDTO.setScore(scoringStrategy.score(node.get("stargazers_count").asLong(),
                node.get("forks_count").asLong(), OffsetDateTime.parse(node.get("pushed_at").asText())));
        items.add(githubRepoDTO);
      }
    }
    GithubSearchResponseDTO githubSearchResponseDTO = new GithubSearchResponseDTO();
    githubSearchResponseDTO.setTotalCount(root.get("total_count").asLong());
    githubSearchResponseDTO.setItems(items);
    return githubSearchResponseDTO;
  }

  public GithubSearchResponseDTO toGithubSearchResponseDTO(long totalCount, List<GithubRepo> repos) {
    GithubSearchResponseDTO githubSearchResponseDTO = new GithubSearchResponseDTO();
    githubSearchResponseDTO.setTotalCount(totalCount);
    if (Objects.nonNull(repos)) {
      List<GithubRepoDTO> items = repos
              .stream()
              .map(item -> {
                return getGithubRepoDTO(item);
              }).collect(Collectors.toList());
      githubSearchResponseDTO.setItems(items);
    }
    return githubSearchResponseDTO;
  }

  public Long toTotalCount(String searchResponse) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(searchResponse);
    return root.get("total_count").asLong();
  }

  private GithubRepoDTO getGithubRepoDTO(GithubRepo item) {
    GithubRepoDTO githubRepoDTO = new GithubRepoDTO();
    githubRepoDTO.setLanguage(item.getLanguage());
    githubRepoDTO.setCreatedDate(item.getCreatedDate());
    githubRepoDTO.setRepositoryId(item.getRepositoryId());
    githubRepoDTO.setUrl(item.getUrl());
    githubRepoDTO.setScore(item.getScore());
    return githubRepoDTO;
  }

}
