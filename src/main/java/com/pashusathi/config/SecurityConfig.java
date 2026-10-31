package com.pashusathi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pashusathi.dto.UserResponse;
import com.pashusathi.exception.ApiError;
import com.pashusathi.security.AppUserDetails;
import com.pashusathi.security.CsrfCookieFilter;
import com.pashusathi.security.SecurityJson;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ObjectMapper mapper) throws Exception {

        AuthenticationSuccessHandler success = (req, res, auth) ->
                SecurityJson.write(mapper, res, HttpStatus.OK,
                        UserResponse.from((AppUserDetails) auth.getPrincipal()));

        // One generic message for every failure, so attackers cannot learn which usernames exist.
        AuthenticationFailureHandler failure = (req, res, ex) ->
                SecurityJson.write(mapper, res, HttpStatus.UNAUTHORIZED,
                        ApiError.of(401, "INVALID_CREDENTIALS", "Username or password is incorrect"));

        AccessDeniedHandler denied = (req, res, ex) ->
                SecurityJson.write(mapper, res, HttpStatus.FORBIDDEN,
                        ApiError.of(403, "ACCESS_DENIED", "You do not have permission to do this"));

        http
                // CSRF: the server sets an XSRF-TOKEN cookie; our JavaScript sends it back
                // in the X-XSRF-TOKEN header on every POST/PUT/PATCH/DELETE.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        // static page shells and assets: the DATA behind them is protected by the API
                        .requestMatchers("/", "/*.html", "/dairy/*.html", "/farmer/*.html",
                                "/css/**", "/js/**", "/i18n/**").permitAll()
                        .requestMatchers("/api/health", "/api/version", "/api/auth/csrf", "/api/auth/register-dairy").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // role rules
                        .requestMatchers("/api/dairy/**").hasRole("DAIRY")
                        .requestMatchers("/api/farmer/**").hasRole("FARMER")
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login.html")                 // also switches off Spring's default login page
                        .loginProcessingUrl("/api/auth/login")    // POST username + password here
                        .successHandler(success)
                        .failureHandler(failure))

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")            // POST, needs the CSRF header
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpStatus.NO_CONTENT.value()))
                        .deleteCookies("JSESSIONID"))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // 401, no redirect
                        .accessDeniedHandler(denied));

        return http.build();
    }
}