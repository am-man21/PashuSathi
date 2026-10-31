package com.pashusathi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pashusathi.entity.Dairy;

public interface DairyRepository extends JpaRepository<Dairy, Long> { }