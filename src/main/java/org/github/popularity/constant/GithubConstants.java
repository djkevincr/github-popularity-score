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
package org.github.popularity.constant;

/**
 * Github Constants.
 *
 * @author Kevin Ratnasekera
 */
public class GithubConstants {

  // github api
  public static String GITHUB_REPOSITORY_SEARCH_QUERY = "/search/repositories?q=language:{language}+created:>{date}&page={offset}&per_page={limit}&sort=updated&order=desc";
  public static String GITHUB_API_VERSION_KEY = "X-GitHub-Api-Version";
  public static String GITHUB_API_VERSION_VALUE = "2022-11-28";
  public static String GITHUB_API_CONTENT_TYPE = "application/vnd.github+json";
  public static String GITHUB_API_AUTHORIZATION_KEY = "Authorization";
  public static String GITHUB_API_AUTHORIZATION_API_KEY_PREFIX = "Bearer ";
  public static String GITHUB_API_ACCEPT_KEY = "Accept";
  public static String GITHUB_API_UNAUTHORIZED = "unauthorized";

  // data fetch
  public static final int GITHUB_SEARCH_PAGE_SIZE = 100;
  public static Integer SERVER_POOL_SIZE = 1;
  public static Integer SERVER_POOL_GRACEFUL_TERMINATION_DURATION = 10;

  // popularity score api
  public static final int MAX_API_PAGE_SIZE = 30;

  // popularity score algorithm
  public static final int STARGAZERS_COUNT_WEIGHT = 4;
  public static final int FORK_COUNT_WEIGHT = 4;
  public static final int LAST_UPDATED_WEIGHT = 2;

  // data mapping
  public static String DATA_FIELD_ITEMS = "items";
  public static String DATA_FIELD_LANGUAGE = "language";
  public static String DATA_FIELD_CREATED_AT = "created_at";
  public static String DATA_FIELD_ID = "id";
  public static String DATA_FIELD_CLONE_URL = "clone_url";
  public static String DATA_FIELD_STARGAZERS_COUNT = "stargazers_count";
  public static String DATA_FIELD_FORKS_COUNT = "forks_count";
  public static String DATA_FIELD_PUSHED_AT = "pushed_at";
  public static String DATA_FIELD_TOTAL_COUNT = "total_count";

}
