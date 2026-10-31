package com.pashusathi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pashusathi.entity.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}