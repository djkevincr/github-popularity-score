# About

Github Repository Popularity Service.

# Author

Kevin Ratnasekera

# Assumptions

* Assigning popularity score to Github repository means retrieve data from Github Search API, calculate score and 
  persisting relevant score to a database. This is done on background thread when main service is started. 
  We name this asynchronous flow. We expose paginated endpoint to expose stored data on database.
  However for performance comparison synchronous endpoint is also implemented where it calls Github search endpoint 
  directly. We name this synchronous flow where we assign popularity score on the fly on Github response.
* Client side who consumes backend API is only interested Github repositories and it s popularity score.
* Configure earliest created date and language means, for the asynchronous flow these params needs to supplied 
  as environment variables and for synchronous flow these will be supplied as query params.
  
# Design Decisions

* Solution is based on Services oriented architecture.
* All REST API endpoints implement pagination, this is to reduce latency and performance overhead with large payloads.
  Max API endpoint page size is 30.
* SQL database is used instead NOSQL, assuming read heavy data access pattern, small size of current data, 
  indexing needs ( pagination ) and query performance. However considering data horizontal scalability with massive 
  data sets we could explore NOSQL with sharding and that is future improvement.
* H2 embedded SQL database used only to demonstrate functional side of the application and keep things simple. 
  These embedded databases are not production ready or scalable. Memory backed store will lose stored 
  in case application crashes all date will be lost. Automate testing can be easier with embedded databases. 
  Assuming this is read heavy system multiple read replicas will be helpful. Ideally we should point to external database 
  with such configuration.
* Using docker for containerization and easy deployment/replication in any environment.
* All API endpoints should be stateless. Deploying applications on multiple machines could increase the throughput. ( TPS )
  It also helps if multiple Github users accounts can be used parallely on machines. This is because of rate limiting 
  on Github API happens at user basis.
* Strategy design pattern is used for Scoring Strategy. So that can we can easily plug add new Scoring Strategies in future. 

# Further improvements

* Currently Github search data fetch logic runs on single threaded on background. We could also parallel 
  batch process with multiple parallel threads executing Github search queries, however due to Github API rate 
  limiting this will add additional complexity to logic. Since we are dealing with large data sets, processing data 
  could take huge amount of time, we could store the progress of the process on database ( page number ) in case 
  application crashes and it s needed to continue the process from where it is stopped/crashed.
* Improve error handling with x-ratelimit-limit, x-ratelimit-remaining, x-ratelimit-used and x-ratelimit-reset 
  response headers from Github search API.
* Use of Maven profiling to keep environment specific variable Eg- DEV, STAGE, PRODUCTION.
* Externalizing sensitive information Eg- Github API key using a secure vault.
* Improve test coverage.
* Improve validations in user inputs. Eg:- date and supported languages.
* Improve data constraints on entities.  
* Implement rate limiting for REST endpoints using Eg:-API Manager, Firewall
* Authorization of API endpoints Eg:-API Manager, Identity servers
* Deploying applications on multiple machines and front with a load balancer. This is for high availability.
* Logging added however can be further improved.  
* Swagger / OPEN API Documentation.

# Scoring Algorithm

* Algorithm is based on weighted average score. This scoring algorithm is implemented simple and straight forward.

```
Stargazers Count - Directly proportional / relationship to popularity score.
Fork Count - Directly proportional / relationship to popularity score.
Last updated/pushed date - Inversely proportional / relationship to popularity score.
```

* We give Stargazers Count and Fork Count an equal weight of 4 and Last updated date a weight of 2. 
  This is assuming Stargazers and Fork Count is a better indicator of popularity of Github Repo compared to updated date.
  For example Last pushed date is much recent for newly created repo. 

* This is how the points will be given for each attribute level.

```
stargazersCount >= 100000 - 4 points
stargazersCount < 100000 && stargazersCount >= 1000 - 3 points
stargazersCount < 1000 && stargazersCount >= 100 - 2 points
stargazersCount < 100 && stargazersCount > 0 - 1 point
```

```
forksCount >= 1000 - 4 points
forksCount < 1000 && forksCount >= 500 - 3 points
forksCount < 500 && forksCount >= 100 - 2 points
forksCount < 100 && forksCount > 0 - 1 point
```

```
daysSinceLastUpdate <= 28 - 2 points
daysSinceLastUpdate > 28 && daysSinceLastUpdate <= 56 - 1 point
```

* each attribute level points are multiplied by respective weight and normalized by total weight. 
  Then we multiply such normalized score by 100, so that our popularity score is between 0 and 100.

# Tech Stack Used

* Java 8 with Spring Boot for Service, Jackson for Json parsing and Okhttp for HTTP client
* Apache Maven 3.9.6

# Building and Running Instructions

* The spring boot application is dockerized and added to the maven build process. 
  So it s not required to docker build manually. Make sure docker is up and running locally before executing maven build command.
  Notice the docker image name and tag used in the below example.
* ```mvn clean install```
* ``` docker run -e SEARCH_LANGUAGE='java' -e SEARCH_CREATED_DATE='2014-02-011' -e GITHUB_API_KEY='ghp_' -e DATA_FETCH_ENABLED='true' -p 8080:8080 github-popularity-score:0.0.1 ```
* The spring boot service will be running at ```localhost:8080```
* Notice the environment variables passed above. Language and created date are inputs for Github Search query. 
  Github API key is used as Authorization header. Data fetch boolean variable indicates whether data fetch from Github 
  should run or not on background. 

# API

## Get Github Popularity Score - Async API flow

GET /api/v1/search/repositories?language=java&createdDate=2014-02-01&offset=0&limit=20

Request Parameters

```
language - language for Github search query.
createdDate - earliest repo created date for Github search query.
offset - page number for pagination.
limit - page size for pagination.
```
Response

```
{
  "totalCount": 17542568,
  "items": [
    {
      "repositoryId": 581546590,
      "url": "https://github.com/kazeulo/The-Land-of-Ice-and-Fire.git",
      "createdDate": "2022-12-23T14:06:06Z",
      "language": "java",
      "score": 11.11111111111111
    },
    {
      "repositoryId": 941952157,
      "url": "https://github.com/Sanjay0001748/MovieStreaming.git",
      "createdDate": "2025-03-03T10:27:34Z",
      "language": "java",
      "score": 11.11111111111111
    }
  ]
}      
```

Returned Status Codes

```
200 - Successful
400 - Bad request
500 - Internal server error
```

## Get Github Popularity Score - Sync API flow

GET /api/v1/search/repositories/sync?language=java&createdDate=2014-02-01&offset=0&limit=20

Request Parameters

```
language - language for Github search query.
createdDate - earliest repo created date for Github search query.
offset - page number for pagination.
limit - page size for pagination.
```

Response

```
{
  "totalCount": 17542568,
  "items": [
    {
      "repositoryId": 581546590,
      "url": "https://github.com/kazeulo/The-Land-of-Ice-and-Fire.git",
      "createdDate": "2022-12-23T14:06:06Z",
      "language": "java",
      "score": 11.11111111111111
    },
    {
      "repositoryId": 941952157,
      "url": "https://github.com/Sanjay0001748/MovieStreaming.git",
      "createdDate": "2025-03-03T10:27:34Z",
      "language": "java",
      "score": 11.11111111111111
    }
  ]
} 
```

Returned Status Codes

```
200 - Successful
400 - Bad request
500 - Internal server error
```

