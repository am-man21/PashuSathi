package com.pashusathi.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

/** Writes JSON from security handlers (which run outside controllers). */
public final class SecurityJson {

    private SecurityJson() { }

    public static void write(ObjectMapper mapper, HttpServletResponse res, HttpStatus status, Object body)
            throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        mapper.writeValue(res.getWriter(), body);
    }
}