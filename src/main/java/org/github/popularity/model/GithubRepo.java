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
package org.github.popularity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Github Repo Domain Class.
 *
 * @author Kevin Ratnasekera
 */
@Table(name = "github_repo", indexes = {@Index(columnList = "language"), @Index(columnList = "repository_id", unique = true), @Index(columnList = "created_date ASC")})
@Entity
public class GithubRepo {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private long id;
  @Column(name = "repository_id")
  private Long repositoryId;
  @Column(name = "url")
  private String url;
  @Column(name = "created_date")
  private OffsetDateTime createdDate;
  @Column(name = "language")
  private String language;
  @Column(name = "score")
  private Double score;
  @Column(name = "scored_date")
  private OffsetDateTime scoredDate;
  @Column(name = "stargazers_count")
  private Long stargazersCount;
  @Column(name = "forks_count")
  private Long forksCount;
  @Column(name = "updated_date")
  private OffsetDateTime updatedDate;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Long getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(Long repositoryId) {
    this.repositoryId = repositoryId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public OffsetDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(OffsetDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public OffsetDateTime getScoredDate() {
    return scoredDate;
  }

  public void setScoredDate(OffsetDateTime scoredDate) {
    this.scoredDate = scoredDate;
  }

  public Long getStargazersCount() {
    return stargazersCount;
  }

  public void setStargazersCount(Long stargazersCount) {
    this.stargazersCount = stargazersCount;
  }

  public Long getForksCount() {
    return forksCount;
  }

  public void setForksCount(Long forksCount) {
    this.forksCount = forksCount;
  }

  public OffsetDateTime getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(OffsetDateTime updatedDate) {
    this.updatedDate = updatedDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GithubRepo that = (GithubRepo) o;
    return Objects.equals(repositoryId, that.repositoryId)
            && Objects.equals(url, that.url)
            && Objects.equals(createdDate, that.createdDate)
            && Objects.equals(language, that.language)
            && Objects.equals(score, that.score) && Objects.equals(scoredDate, that.scoredDate)
            && Objects.equals(stargazersCount, that.stargazersCount)
            && Objects.equals(forksCount, that.forksCount)
            && Objects.equals(updatedDate, that.updatedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repositoryId, url, createdDate, language, score, scoredDate,
            stargazersCount, forksCount, updatedDate);
  }
}
