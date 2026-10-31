package com.pashusathi;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.pashusathi.entity.AppUser;
import com.pashusathi.entity.Dairy;
import com.pashusathi.entity.Role;
import com.pashusathi.repository.DairyRepository;
import com.pashusathi.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional          // every test is rolled back, so tests do not disturb each other
class AuthFlowTest {

    private static final String PASSWORD = "Passw0rd!x";

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired DairyRepository dairies;
    @Autowired PasswordEncoder encoder;

    // ---------- helpers ----------
    private String registerJson(String username, String password) {
        return """
            {"dairyName":"Test Dairy","fullName":"Test Owner","username":"%s","password":"%s"}
            """.formatted(username, password);
    }

    private void createUser(String username, Role role) {
        Dairy dairy = dairies.save(new Dairy("Test Dairy", null, null));
        users.save(new AppUser(dairy, username, encoder.encode(PASSWORD), "Test User", role));
    }

    private MockHttpSession login(String username) throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/login").with(csrf())
                        .param("username", username).param("password", PASSWORD))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    // ---------- registration ----------
    @Test
    void registerDairy_createsDairyUser() throws Exception {
        mvc.perform(post("/api/auth/register-dairy").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(registerJson("dairy_one", PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("DAIRY"))
                .andExpect(jsonPath("$.username").value("dairy_one"));
    }

    @Test
    void registerWithoutCsrfToken_isRejected() throws Exception {
        mvc.perform(post("/api/auth/register-dairy")
                        .contentType(MediaType.APPLICATION_JSON).content(registerJson("dairy_two", PASSWORD)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void duplicateUsername_returns409() throws Exception {
        String body = registerJson("dairy_three", PASSWORD);
        mvc.perform(post("/api/auth/register-dairy").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/register-dairy").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USERNAME_TAKEN"));
    }

    @Test
    void weakPassword_returns400WithFieldError() throws Exception {
        mvc.perform(post("/api/auth/register-dairy").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(registerJson("dairy_four", "short")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    // ---------- login ----------
    @Test
    void login_withValidCredentials_returnsUser() throws Exception {
        createUser("farmer1", Role.FARMER);
        mvc.perform(post("/api/auth/login").with(csrf())
                        .param("username", "farmer1").param("password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("FARMER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        createUser("farmer2", Role.FARMER);
        mvc.perform(post("/api/auth/login").with(csrf())
                        .param("username", "farmer2").param("password", "WrongPass1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void login_unknownUser_returnsSameError() throws Exception {
        mvc.perform(post("/api/auth/login").with(csrf())
                        .param("username", "nobody").param("password", PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    // ---------- /me ----------
    @Test
    void me_withoutLogin_returns401() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void me_afterLogin_returnsCurrentUser() throws Exception {
        createUser("dairy1", Role.DAIRY);
        MockHttpSession session = login("dairy1");
        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dairy1"))
                .andExpect(jsonPath("$.role").value("DAIRY"));
    }

    // ---------- role restrictions ----------
    @Test
    void farmerCannotCallDairyEndpoint() throws Exception {
        createUser("farmer3", Role.FARMER);
        mvc.perform(get("/api/dairy/ping").session(login("farmer3")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void dairyCannotCallFarmerEndpoint() throws Exception {
        createUser("dairy2", Role.DAIRY);
        mvc.perform(get("/api/farmer/ping").session(login("dairy2")))
                .andExpect(status().isForbidden());
    }

    @Test
    void dairyCanCallDairyEndpoint() throws Exception {
        createUser("dairy3", Role.DAIRY);
        mvc.perform(get("/api/dairy/ping").session(login("dairy3")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("hello dairy"));
    }

    @Test
    void farmerCanCallFarmerEndpoint() throws Exception {
        createUser("farmer4", Role.FARMER);
        mvc.perform(get("/api/farmer/ping").session(login("farmer4")))
                .andExpect(status().isOk());
    }

    // ---------- logout ----------
    @Test
    void logout_endsTheSession() throws Exception {
        createUser("dairy4", Role.DAIRY);
        MockHttpSession session = login("dairy4");
        mvc.perform(post("/api/auth/logout").with(csrf()).session(session))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }
}