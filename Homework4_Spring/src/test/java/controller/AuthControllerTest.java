package controller;

import io.envoi.controller.AuthController;
import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;
    private String testEmail = "test@example.com";
    private String testPassword = "password";
    private String testName = "Test User";
    private Account testAccount = new Account(testEmail, testPassword, testName, Roles.USER);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("Registration successful")
    void testRegistration_Success() throws Exception {
        when(accountService.emailExists(testEmail)).thenReturn(false);
        when(accountService.save(any(Account.class))).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .param("email", testEmail)
                        .param("password", testPassword)
                        .param("name", testName))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Account created successfully"));
    }

    @Test
    void testRegistration_EmailExists() {
        when(accountService.emailExists(testEmail)).thenReturn(true);

        ResponseEntity<?> response = authController.registration(testEmail, testPassword, testName);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
        verify(accountService).emailExists(testEmail);
        verify(accountService, never()).save(any());
    }

    @Test
    @DisplayName("Registration conflict - Email already exists")
    void testRegistration_Conflict() throws Exception {
        when(accountService.emailExists(testEmail)).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .param("email", testEmail)
                        .param("password", testPassword)
                        .param("name", testName))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Email already exists"));

        verify(accountService).emailExists(testEmail);
        verify(accountService, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Registration failed - Unable to save account")
    void testRegistration_Failure() throws Exception {
        when(accountService.emailExists(testEmail)).thenReturn(false);
        when(accountService.save(any(Account.class))).thenReturn(false);

        mockMvc.perform(post("/api/auth/register")
                        .param("email", testEmail)
                        .param("password", testPassword)
                        .param("name", testName))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Failed to create account"));

        verify(accountService).save(any(Account.class));
    }
    @Test
    @DisplayName("Login successful")
    void testLogin_Success() throws Exception {
        when(accountService.getByEmail(testEmail)).thenReturn(testAccount);

        mockMvc.perform(post("/api/auth/login")
                        .param("email", testEmail)
                        .param("password", testPassword))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Login successful"));
    }

    @Test
    @DisplayName("Login failed - Invalid email")
    void testLogin_InvalidEmail() throws Exception {
        String invalidEmail = "invalid-email";

        mockMvc.perform(post("/api/auth/login")
                        .param("email", invalidEmail)
                        .param("password", testPassword))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Invalid email"));
    }

    @Test
    @DisplayName("Login failed - Invalid password")
    void testLogin_InvalidPassword() throws Exception {
        when(accountService.getByEmail(testEmail)).thenReturn(testAccount);

        mockMvc.perform(post("/api/auth/login")
                        .param("email", testEmail)
                        .param("password", "wrong-password")) // неверный пароль
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Invalid email or password"));
    }
}