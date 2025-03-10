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
package org.github.popularity.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;

import static org.github.popularity.constant.GithubConstants.GITHUB_API_VERSION_KEY;
import static org.github.popularity.constant.GithubConstants.GITHUB_API_VERSION_VALUE;
import static org.github.popularity.constant.GithubConstants.GITHUB_API_ACCEPT_KEY;
import static org.github.popularity.constant.GithubConstants.GITHUB_API_CONTENT_TYPE;
import static org.github.popularity.constant.GithubConstants.GITHUB_REPOSITORY_SEARCH_QUERY;
import static org.github.popularity.constant.GithubConstants.GITHUB_API_AUTHORIZATION_API_KEY_PREFIX;
import static org.github.popularity.constant.GithubConstants.GITHUB_API_UNAUTHORIZED;
import static org.github.popularity.constant.GithubConstants.GITHUB_API_AUTHORIZATION_KEY;

/**
 * Github HTTP Client Implementation.
 *
 * @author Kevin Ratnasekera
 */
@Component
public class GithubHTTPClient implements Client {

  @Value("${github.base.url}")
  private String githubBaseUrl;

  @Value("${github.api.key}")
  private String githubAPIKey;

  private OkHttpClient client;

  @PostConstruct
  public void init() {
    client = new OkHttpClient();
  }

  public HTTPResponse sendSearchRequest(String language,
                                        LocalDate createdDate,
                                        int offset,
                                        int limit) throws IOException {
    String url = githubBaseUrl + GITHUB_REPOSITORY_SEARCH_QUERY
            .replace("{language}", language)
            .replace("{date}", createdDate.toString())
            .replace("{limit}", String.valueOf(limit))
            .replace("{offset}", String.valueOf(offset));
    Request.Builder requestBuilder = new Request.Builder()
            .addHeader(GITHUB_API_VERSION_KEY, GITHUB_API_VERSION_VALUE)
            .addHeader(GITHUB_API_ACCEPT_KEY, GITHUB_API_CONTENT_TYPE);
    if (!GITHUB_API_UNAUTHORIZED.equals(githubAPIKey)) {
      requestBuilder.addHeader(GITHUB_API_AUTHORIZATION_KEY,
              GITHUB_API_AUTHORIZATION_API_KEY_PREFIX + githubAPIKey);
    }
    Request request = requestBuilder
            .url(url)
            .build();
    return new HTTPResponse(client.newCall(request).execute());
  }

}
