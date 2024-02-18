package com.repositoryinfo.api.services.impl;

import com.repositoryinfo.api.config.GitHubConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RepositoryServiceImplTest {
    @Mock
    private GitHubConfig gitHubConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getRepositoriesInfo() {
        Object[] repositories = new Object[1];
        Map<String, Object> repository = new HashMap<>();
        repository.put("name", "example-repo");
        repository.put("fork", false);
        Map<String, Object> owner = new HashMap<>();
        owner.put("login", "example-owner");
        repository.put("owner", owner);
        repository.put("branches_url", "https://api.github.com/repos/example-user/example-repo/branches/{/branch}");
        repositories[0] = repository;

        ResponseEntity<Object[]> branchesResponse = ResponseEntity.ok(new Object[]{});
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token test-token");
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        when(gitHubConfig.getGitHubAccessToken()).thenReturn("test-token");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(entity), eq(Object[].class)))
                .thenReturn(branchesResponse);

        List<Map<String, Object>> result = repositoryService.getRepositoriesInfo(repositories);

        assertEquals(1, result.size());
        Map<String, Object> repoInfo = result.get(0);
        assertEquals("example-repo", repoInfo.get("Repository Name"));
        assertEquals("example-owner", repoInfo.get("Owner Login"));
    }

    @Test
    void getBranchesInfo() {
        Object[] branches = new Object[1];
        Map<String, Object> branch = new HashMap<>();
        branch.put("name", "main");
        Map<String, Object> commit = new HashMap<>();
        commit.put("sha", "abc123");
        branch.put("commit", commit);
        branches[0] = branch;

        List<Map<String, Object>> result = repositoryService.getBranchesInfo(branches);

        assertEquals(1, result.size());
        Map<String, Object> branchInfo = result.get(0);
        assertEquals("main", branchInfo.get("Branch Name"));
        assertEquals("abc123", branchInfo.get("Commit sha"));
    }
}