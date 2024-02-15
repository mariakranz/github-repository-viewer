package com.repositoryinfo.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class RepositoryController {

    @GetMapping("healthcheck")
    public HttpStatus getHealth(){
        return HttpStatus.OK;
    }

}
