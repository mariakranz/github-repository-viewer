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
    public RepositoryController(RepositoryService repoService, GitHubConfig gitHubConfig) {
        this.repoService = repoService;
        this.gitHubConfig = gitHubConfig;
    }

    private RepositoryService repoService;
    private final GitHubConfig gitHubConfig;

    @GetMapping("healthcheck")
    public HttpStatus getHealth(){
        return HttpStatus.OK;
    }

    @GetMapping("repositories2/{username}")
    public ResponseEntity<Object> getUserRepositories2(@PathVariable String username){
        String url = "https://api.github.com/users/" + username + "/repos";
        String accessToken = gitHubConfig.getGitHubAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Object[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object[].class);
            return ResponseEntity.ok(repoService.getRepositoriesInfo(response.getBody()));
        } catch (HttpClientErrorException.NotFound e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", e.getStatusCode().value());                     //404
            errorResponse.put("message", "User not found");
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());      //500
            errorResponse.put("message", "Unexpected error");
            return ResponseEntity.status((HttpStatusCode) errorResponse.get("status")).body(errorResponse);
        }
    }
}
