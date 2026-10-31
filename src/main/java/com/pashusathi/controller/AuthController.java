package com.pashusathi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pashusathi.dto.RegisterDairyRequest;
import com.pashusathi.dto.UserResponse;
import com.pashusathi.security.AppUserDetails;
import com.pashusathi.service.AuthService;

import jakarta.validation.Valid;

/** Login and logout are handled by Spring Security (see SecurityConfig). */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Calling this once makes the browser receive the XSRF-TOKEN cookie. */
    @GetMapping("/csrf")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void csrf() { }

    @PostMapping("/register-dairy")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerDairy(@Valid @RequestBody RegisterDairyRequest request) {
        return authService.registerDairy(request);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AppUserDetails user) {
        return UserResponse.from(user);
    }
}