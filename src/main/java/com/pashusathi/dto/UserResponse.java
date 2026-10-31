package com.pashusathi.dto;

import com.pashusathi.entity.AppUser;
import com.pashusathi.security.AppUserDetails;

/** What we send to the browser about a user. It never contains the password hash. */
public record UserResponse(Long id, String username, String fullName, String role,
                           Long dairyId, String language, boolean mustChangePassword) {

    public static UserResponse from(AppUser u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getRole().name(),
                u.getDairyId(), u.getLanguagePref(), u.isMustChangePassword());
    }

    public static UserResponse from(AppUserDetails u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getRole().name(),
                u.getDairyId(), u.getLanguage(), u.isMustChangePassword());
    }
}