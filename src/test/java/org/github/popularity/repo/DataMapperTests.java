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

import org.github.popularity.dto.GithubRepoDTO;
import org.github.popularity.dto.GithubSearchResponseDTO;
import org.github.popularity.mapper.DataMapper;
import org.github.popularity.model.GithubRepo;
import org.github.popularity.scoring.WeightedScoringStrategy;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class DataMapperTests {

  private static List<String>  urls = new ArrayList<>();
  private static List<Long>  repoIds = new ArrayList<>();

  static {
    // add test data urls
    urls.add("https://github.com/Ba781741/CIS4340a.git");
    urls.add("https://github.com/Juyoung8563/auto_monitoring.git");
    urls.add("https://github.com/EliasBasil/CapstoneProject2.git");
    urls.add("https://github.com/Abdulaziz-ENG/Calculator.git");
    urls.add("https://github.com/John1Souza/estudos-angular.git");
    urls.add("https://github.com/LLEGIT/epsi_m1_final_project_big_data.git");
    urls.add("https://github.com/propaganda-and-fabrication/outsourcing-app.git");
    urls.add("https://github.com/CoffeeCoder1/VendorTools.git");
    urls.add("https://github.com/ARKENOP/PetStore.git");
    urls.add("https://github.com/dochmai382/auto_monitoring.git");
    urls.add("https://github.com/vectorqi/WatchFace.git");
    urls.add("https://github.com/SNH277/WarZone-Game.git");
    urls.add("https://github.com/jaortiz16/Payment_Gateway_Transacciones.git");
    urls.add("https://github.com/greenem-official/RouteNavigator.git");
    urls.add("https://github.com/ivanserrano226/REQ1.git");
    urls.add("https://github.com/DenisBuserski/db-investments-tracker.git");
    urls.add("https://github.com/oskhox/Uppgifter_Funktionell_programmering.git");
    urls.add("https://github.com/Jalller/MovieDB.git");
    urls.add("https://github.com/cnncnncom/auto_monitoring.git");
    urls.add("https://github.com/Tadeo-2404/Terraform-Backend.git");

    // add test data repository ids
    repoIds.add(Long.valueOf(941684790));
    repoIds.add(Long.valueOf(939807181));
    repoIds.add(Long.valueOf(941693605));
    repoIds.add(Long.valueOf(939411798));
    repoIds.add(Long.valueOf(941272546));
    repoIds.add(Long.valueOf(941237945));
    repoIds.add(Long.valueOf(940406386));
    repoIds.add(Long.valueOf(934413162));
    repoIds.add(Long.valueOf(941693167));
    repoIds.add(Long.valueOf(939807132));
    repoIds.add(Long.valueOf(940307313));
    repoIds.add(Long.valueOf(940301368));
    repoIds.add(Long.valueOf(936421640));
    repoIds.add(Long.valueOf(932359317));
    repoIds.add(Long.valueOf(941694200));
    repoIds.add(Long.valueOf(935702993));
    repoIds.add(Long.valueOf(929820209));
    repoIds.add(Long.valueOf(941672666));
    repoIds.add(Long.valueOf(939807400));
    repoIds.add(Long.valueOf(941351273));
  }

  @Autowired
  private DataMapper dataMapper;

  @Test
  public void contexLoads() {
    assertThat(dataMapper).isNotNull();
  }

  private static final String TEST_FILE = "test.json";

  @Test
  public void toTotalCountDataMapperTest() throws IOException {
    Path filePath = Paths.get(getClass().getClassLoader().getResource(TEST_FILE).getPath());
    String content = new String(Files.readAllBytes(Paths.get(filePath.toUri())));
    long totalCount = dataMapper.toTotalCount(content);
    Assert.assertEquals(totalCount, 222223L);
  }

  @Test
  public void toGithubSearchResponseDTODataMapperTest() throws IOException {
    Path filePath = Paths.get(getClass().getClassLoader().getResource(TEST_FILE).getPath());
    String content = new String(Files.readAllBytes(Paths.get(filePath.toUri())));
    GithubSearchResponseDTO responseDTO = dataMapper.toGithubSearchResponseDTO(content, new WeightedScoringStrategy());
    Assert.assertEquals(responseDTO.getTotalCount(), Long.valueOf(222223L));
    Assert.assertEquals(responseDTO.getItems().size(), 20);
    long javaRepoCount = responseDTO.getItems().stream().filter(item -> item.getLanguage().equals("java")).count();
    Assert.assertEquals(javaRepoCount, 20);

    int pointer = 0;
    for(GithubRepoDTO item : responseDTO.getItems()) {
      // url
      Assert.assertEquals(item.getUrl(), urls.get(pointer));
      // repository id
      Assert.assertEquals(item.getRepositoryId(), repoIds.get(pointer));
      // language
      Assert.assertEquals(item.getLanguage(), "java");
      // do same for other properties
      pointer++;
    }
  }

  @Test
  public void toGithubRepoDataMapperTest() throws IOException {
    Path filePath = Paths.get(getClass().getClassLoader().getResource(TEST_FILE).getPath());
    String content = new String(Files.readAllBytes(Paths.get(filePath.toUri())));
    List<GithubRepo> repos = dataMapper.toGithubRepo(content, new WeightedScoringStrategy());
    Assert.assertEquals(repos.size(), 20);
    long javaRepoCount = repos.stream().filter(item -> item.getLanguage().equals("java")).count();
    Assert.assertEquals(javaRepoCount, 20);
    int pointer = 0;
    for(GithubRepo item : repos) {
      // url
      Assert.assertEquals(item.getUrl(), urls.get(pointer));
      // repository id
      Assert.assertEquals(item.getRepositoryId(), repoIds.get(pointer));
      // language
      Assert.assertEquals(item.getLanguage(), "java");
      // do same for other properties
      pointer++;
    }
  }

}
