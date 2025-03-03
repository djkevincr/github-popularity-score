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

import org.github.popularity.model.GithubRepo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Github Repo data Repository.
 *
 * @author Kevin Ratnasekera
 */
@Repository
public interface GithubRepository extends CrudRepository<GithubRepo, Long> {

  @Query(value = "SELECT * FROM github_repo WHERE language =?1 AND created_date >= ?2 ORDER BY created_date ASC LIMIT ?3 OFFSET ?4",
          nativeQuery = true)
  List<GithubRepo> fetchGithubRepos(String language,
                                    OffsetDateTime created_date,
                                    Integer limit,
                                    Integer offset);

  @Query(value = "SELECT COUNT(*) FROM github_repo WHERE language =?1 AND created_date >= ?2",
          nativeQuery = true)
  Long countGithubRepos(String language, OffsetDateTime created_date);

  GithubRepo findByRepositoryId(Long repositoryId);

}
