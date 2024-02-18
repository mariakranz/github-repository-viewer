package com.repositoryinfo.api.controllers;

import com.repositoryinfo.api.config.GitHubConfig;
import com.repositoryinfo.api.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class RepositoryController {

    @Autowired
    public RepositoryController(RepositoryService repoService, GitHubConfig gitHubConfig, RestTemplate restTemplate) {
        this.repoService = repoService;
        this.gitHubConfig = gitHubConfig;
        this.restTemplate = restTemplate;
    }
    private RestTemplate restTemplate;

    private RepositoryService repoService;
    private final GitHubConfig gitHubConfig;

    @GetMapping("healthcheck")
    public HttpStatus getHealth(){
        return HttpStatus.OK;
    }

    @GetMapping("repositories/{username}")
    public ResponseEntity<Object> getUserRepositories(@PathVariable String username){
        String url = "https://api.github.com/users/" + username + "/repos";
        String accessToken = gitHubConfig.getGitHubAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Object[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object[].class);
            return ResponseEntity.ok(repoService.getRepositoriesInfo(response.getBody()));
        }catch (HttpClientErrorException.NotFound e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", e.getStatusCode().value());
            errorResponse.put("message", "User not found");
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        }catch (HttpClientErrorException.Forbidden | HttpClientErrorException.TooManyRequests e)  {                                //403
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", e.getStatusCode().value());
            errorResponse.put("message", "Rate limit error");
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        }catch (HttpClientErrorException.BadRequest | HttpClientErrorException.UnprocessableEntity e){
            Object exceptionResponse = e.getResponseBodyAs(Object.class);
            Map<String, Object> responseDetails = (Map<String, Object>) exceptionResponse;

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", e.getStatusCode().value());
            errorResponse.put("message", responseDetails.get("message"));
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        }catch (IllegalArgumentException e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("message", "Error executing branches request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }
}

