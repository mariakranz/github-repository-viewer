# Repository Info API
This is a Spring Boot application that serves as an API for retrieving information about GitHub repositories.
## Used technologies and frameworks
- Java 21
- Spring Boot 3
- Maven
- Mockito
- Postman for endpoints testing


## How to use 
1. Clone the repository. 
2. Ensure you have Java and Maven installed.
3. Configure your GitHub access token in the application.properties file. Check the [Github-token](#GITHUB-TOKEN) section.
4. Build and run the application using preferred IDE.


### Github Token
In order for application to work, you must declare the 'accessToken' field with your GitHub access token in the application.properties file:
```java
accessToken=your_generated_gh_access_token
```

I recomend for **github personal token** listed scopes:
- repo:status,
- public_repo,
- read:user.

## App overview
The application has two endpoints healthcheck (returns OK http status) and the endpoint that performs the task (lists info about the repositories).  
### Usage
To use the Repository Info API, make a GET request to the **/api/repositories/{username}** endpoint, where **{username}** is the GitHub username of the user whose repositories you want to retrieve. 
The endpoint returns JSON object with the information about user's repository or
in case of exceptions, the exception code with message what happened.  
This endpoint handles and produces suitable message for listed reasons:
- x-rate limit (403 and 429 http status codes),
- when user does not exist (404 http status code),
- if wrong JSON format is sent (400 status code),
- if wrong parameters are sent (422 status code).
### Api responses
Successful Response
```json
{
  "Owner Login": "example-user",
  "Branches": [
    {
      "Commit sha": "abc123",
      "Branch Name": "main"
    },
    {
      "Branch Name": "feature-branch",
      "Commit SHA": "def456"
    }
  ],
  "Repository Name": "example-repo"
}
```
Error Responses
- **404 Not Found**: If the specified GitHub user does not exist.
```json
{
  "status": 404,
  "message": "User not found"
}
```
### Unit test
Unit tests have been made with the usage of the Mockito framework.
## Contributors
- Maria Kranz
