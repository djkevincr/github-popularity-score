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
  public static String GITHUB_REPOSITORY_SEARCH_QUERY = "/search/repositories?q=language:{language}+created:>{date}&page={offset}&per_page={limit}&sort=updated&order=desc";
  public static String GITHUB_API_VERSION_KEY = "X-GitHub-Api-Version";
  public static String GITHUB_API_VERSION_VALUE = "2022-11-28";
  public static String GITHUB_API_CONTENT_TYPE = "application/vnd.github+json";
  public static String GITHUB_API_AUTHORIZATION_KEY = "Authorization";
  public static String GITHUB_API_ACCEPT_KEY = "Accept";

  public static final int GITHUB_SEARCH_PAGE_SIZE = 100;

  public static Integer SERVER_POOL_SIZE = 1;
  public static Integer SERVER_POOL_GRACEFUL_TERMINATION_DURATION = 10;

  public static String GITHUB_API_UNAUTHORIZED = "unauthorized";

  public static final int MAX_API_PAGE_SIZE = 30;
}
