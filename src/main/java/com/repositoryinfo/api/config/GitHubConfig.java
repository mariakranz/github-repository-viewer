package com.repositoryinfo.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubConfig {

    @Value("${accessToken}")
    private String accessToken;

    public String getGitHubAccessToken() {
        return accessToken;
    }
}
