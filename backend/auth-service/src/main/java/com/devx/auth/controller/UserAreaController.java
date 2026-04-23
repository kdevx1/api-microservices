package com.devx.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserAreaController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String user() {
        return "Área USER";
    }
}