package com.repositoryinfo.api.controllers;

import com.repositoryinfo.api.config.GitHubConfig;
import com.repositoryinfo.api.services.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RepositoryControllerTest {
    @Mock
    private RepositoryService repositoryService;

    @Mock
    private GitHubConfig gitHubConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RepositoryController repositoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    private List<Map<String, Object>> someListOfRepositoryInfo() {
        List<Map<String, Object>> repositories = new ArrayList<>();
        Map<String, Object> repository = new HashMap<>();
        repository.put("name", "example-repo");
        repository.put("fork", false);
        repositories.add(repository);
        return repositories;
    }


    @Test
    public void testGetUserRepositories_UserFound() {
        String username = "exampleUser";
        Object[] repositories = new Object[1];
        Map<String, Object> repository = new HashMap<>();
        repository.put("name", "example-repo");
        repository.put("fork", false);
        Map<String, Object> owner = new HashMap<>();
        owner.put("login", "example-owner");
        repository.put("owner", owner);
        repositories[0] = repository;

        when(gitHubConfig.getGitHubAccessToken()).thenReturn("test-token");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object[].class)))
                .thenReturn(ResponseEntity.ok(repositories));
        when(repositoryService.getRepositoriesInfo(repositories)).thenReturn(someListOfRepositoryInfo());

        ResponseEntity<Object> response = repositoryController.getUserRepositories(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetUserRepositories_UserNotFound() {
        String username = "exampleUser";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token test-token");
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        when(gitHubConfig.getGitHubAccessToken()).thenReturn("test-token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(Object[].class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "not found", null, null, null));

        ResponseEntity<Object> response = repositoryController.getUserRepositories(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(404, responseBody.get("status"));
        assertEquals("User not found", responseBody.get("message"));
    }
}