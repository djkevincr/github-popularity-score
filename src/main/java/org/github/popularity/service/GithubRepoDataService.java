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

import java.time.LocalDate;

/**
 * Github Data Service Interface.
 *
 * @author Kevin Ratnasekera
 */
public interface GithubRepoDataService {

  /**
   * Search Github repository data stored in Database.
   *
   * @param language
   * @param createdDate
   * @param offset
   * @param limit
   *
   * @return GithubSearchResponseDTO paginated data.
   *
   */
  GithubSearchResponseDTO searchFromDatabase(String language,
                                             LocalDate createdDate,
                                             int offset,
                                             int limit);

  /**
   * Search Github repository from Github search endpoint.
   *
   * @param language
   * @param createdDate
   * @param offset
   * @param limit
   *
   * @return GithubSearchResponseDTO paginated data.
   *
   */
  GithubSearchResponseDTO searchFromAPI(String language,
                                        LocalDate createdDate,
                                        int offset,
                                        int limit);

}
