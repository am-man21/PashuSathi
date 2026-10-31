package com.pashusathi.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pashusathi.entity.AppUser;
import com.pashusathi.entity.Role;

/** The logged-in user as Spring Security sees it. It is kept in the HTTP session. */
public class AppUserDetails implements UserDetails, CredentialsContainer {

    private final Long id;
    private final Long dairyId;
    private final String username;
    private String passwordHash;
    private final String fullName;
    private final Role role;
    private final String language;
    private final boolean enabled;
    private final boolean mustChangePassword;

    private AppUserDetails(AppUser u) {
        this.id = u.getId();
        this.dairyId = u.getDairyId();
        this.username = u.getUsername();
        this.passwordHash = u.getPasswordHash();
        this.fullName = u.getFullName();
        this.role = u.getRole();
        this.language = u.getLanguagePref();
        this.mustChangePassword = u.isMustChangePassword();
        // the account works only if the user AND the dairy are active
        this.enabled = u.isEnabled() && "ACTIVE".equals(u.getDairy().getStatus());
    }

    public static AppUserDetails from(AppUser u) { return new AppUserDetails(u); }

    public Long getId() { return id; }
    public Long getDairyId() { return dairyId; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }
    public String getLanguage() { return language; }
    public boolean isMustChangePassword() { return mustChangePassword; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return username; }
    @Override public boolean isEnabled() { return enabled; }

    /** Spring calls this after login so the hash does not stay in the session. */
    @Override
    public void eraseCredentials() { this.passwordHash = null; }
}