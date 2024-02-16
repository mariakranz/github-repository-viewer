package com.repositoryinfo.api.services;

import java.util.List;
import java.util.Map;

public interface RepositoryService {
    List<Map<String, Object>> getRepositoriesInfo(Object[] repository);
}
