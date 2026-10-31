package com.pashusathi.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pashusathi.security.AppUserDetails;

/** TEMPORARY: only used to prove the role rules work. Delete in Step 4. */
@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping("/dairy/ping")
    public Map<String, Object> dairyPing(@AuthenticationPrincipal AppUserDetails u) {
        return Map.of("message", "hello dairy", "dairyId", u.getDairyId());
    }

    @GetMapping("/farmer/ping")
    public Map<String, Object> farmerPing(@AuthenticationPrincipal AppUserDetails u) {
        return Map.of("message", "hello farmer", "dairyId", u.getDairyId());
    }
}