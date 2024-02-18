package com.repositoryinfo.api.services.impl;

import com.repositoryinfo.api.config.GitHubConfig;
import com.repositoryinfo.api.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RepositoryServiceImpl implements RepositoryService {

    @Autowired(required = true)
    public RepositoryServiceImpl(GitHubConfig gitHubConfig, RestTemplate restTemplate) {
        this.gitHubConfig = gitHubConfig;
        this.restTemplate = restTemplate;
    }

    private RestTemplate restTemplate;

    private final GitHubConfig gitHubConfig;

    @Override
    public List<Map<String, Object>> getRepositoriesInfo(Object[] repositories) throws IllegalArgumentException {
        if (repositories.length == 0) return new LinkedList<>();
        List<Map<String, Object>> result = new LinkedList<>();
        for (Object repository : repositories) {
            Map<String, Object> repoDetails = (Map<String, Object>) repository;

            if(!((Boolean) repoDetails.get("fork"))){
                Map<String, Object> repoInfo = new HashMap<>();

                repoInfo.put("Repository Name", repoDetails.get("name"));

                Map<String, Object> ownerInfo = (Map<String, Object>)  repoDetails.get("owner");
                repoInfo.put("Owner Login", ownerInfo.get("login"));

                String branchesUrl = (String) repoDetails.get("branches_url");
                branchesUrl = branchesUrl.replace("{/branch}", "");

                String accessToken = gitHubConfig.getGitHubAccessToken();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "token " + accessToken);
                headers.set("Accept", "application/json");
                HttpEntity<String> entity = new HttpEntity<>(headers);

                try{
                    ResponseEntity<Object[]> branchesResponse = restTemplate.exchange(branchesUrl, HttpMethod.GET, entity, Object[].class);
                    List<Map<String, Object>> branchesInfo = getBranchesInfo(branchesResponse.getBody());
                    if(!branchesInfo.isEmpty()) {
                        repoInfo.put("Branches", branchesInfo);
                    }
                    result.add(repoInfo);

                }catch(HttpClientErrorException.NotFound | HttpClientErrorException.Forbidden |
                        HttpClientErrorException.TooManyRequests | HttpClientErrorException.BadRequest |
                        HttpClientErrorException.UnprocessableEntity e){
                    Object exceptionResponse = e.getResponseBodyAs(Object.class);
                    Map<String, Object> responseDetails = (Map<String, Object>) exceptionResponse;
                    throw new IllegalArgumentException(responseDetails.get("message").toString());

                }

            }
        }
        return result;
    }

    public List<Map<String, Object>> getBranchesInfo(Object[] branches){
        if (branches.length == 0) return new LinkedList<>();
        List<Map<String, Object>> branchesList = new LinkedList<>();
        for (Object branch: branches){
            Map<String, Object> branchesDetails = (Map<String, Object>) branch;

            Map<String, Object> branchesInfo = new HashMap<>();
            branchesInfo.put("Branch Name", branchesDetails.get("name"));

            Map<String, Object> commitInfo = (Map<String, Object>)  branchesDetails.get("commit");
            branchesInfo.put("Commit sha", commitInfo.get("sha"));

            branchesList.add(branchesInfo);
        }
        return branchesList;
    }
}
