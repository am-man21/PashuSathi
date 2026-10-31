package com.pashusathi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pashusathi.dto.RegisterDairyRequest;
import com.pashusathi.dto.UserResponse;
import com.pashusathi.entity.AppUser;
import com.pashusathi.entity.Dairy;
import com.pashusathi.entity.Role;
import com.pashusathi.exception.ApiException;
import com.pashusathi.repository.DairyRepository;
import com.pashusathi.repository.UserRepository;

@Service
public class AuthService {

    private final DairyRepository dairies;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthService(DairyRepository dairies, UserRepository users, PasswordEncoder encoder) {
        this.dairies = dairies;
        this.users = users;
        this.encoder = encoder;
    }

    /** Creates a dairy and its first DAIRY user. Both are saved together or not at all. */
    @Transactional
    public UserResponse registerDairy(RegisterDairyRequest req) {
        String username = req.username().trim();
        if (users.existsByUsername(username)) {
            throw ApiException.conflict("USERNAME_TAKEN", "This username is already in use");
        }
        Dairy dairy = dairies.save(new Dairy(req.dairyName().trim(), req.address(), req.contactPhone()));
        AppUser user = new AppUser(dairy, username, encoder.encode(req.password()),
                req.fullName().trim(), Role.DAIRY);
        users.save(user);
        return UserResponse.from(user);
    }
}