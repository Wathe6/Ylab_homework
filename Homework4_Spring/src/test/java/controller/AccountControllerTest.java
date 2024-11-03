package controller;

import io.envoi.controller.AccountController;
import io.envoi.model.Account;
import io.envoi.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    private final Long testId = 1L;
    private final String testNewEmail = "newemail@example.com";
    private final String testNewName = "New Name";
    private final Account testAccount = new Account();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    @DisplayName("Change email successful")
    void testChangeEmail_Success() throws Exception {
        when(accountService.get(testId)).thenReturn(testAccount);
        when(accountService.update(testAccount)).thenReturn(true);

        mockMvc.perform(patch("/api/accounts/{id}/email", testId)
                        .param("newEmail", testNewEmail))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Email updated successfully"));
    }

    @Test
    @DisplayName("Change email - Account not found")
    void testChangeEmail_NotFound() throws Exception {
        when(accountService.get(testId)).thenReturn(null);

        mockMvc.perform(patch("/api/accounts/{id}/email", testId)
                        .param("newEmail", testNewEmail))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Account not found"));
    }

    @Test
    @DisplayName("Change email - Failed to update account")
    void testChangeEmail_Failure() throws Exception {
        when(accountService.get(testId)).thenReturn(testAccount);
        when(accountService.update(testAccount)).thenReturn(false);

        mockMvc.perform(patch("/api/accounts/{id}/email", testId)
                        .param("newEmail", testNewEmail))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Failed to update account"));
    }

    @Test
    @DisplayName("Change name successful")
    void testChangeName_Success() throws Exception {
        when(accountService.get(testId)).thenReturn(testAccount);
        when(accountService.update(testAccount)).thenReturn(true);

        mockMvc.perform(patch("/api/accounts/{id}/name", testId)
                        .param("newName", testNewName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Name updated successfully"));
    }

    @Test
    @DisplayName("Change name - Account not found")
    void testChangeName_NotFound() throws Exception {
        when(accountService.get(testId)).thenReturn(null);

        mockMvc.perform(patch("/api/accounts/{id}/name", testId)
                        .param("newName", testNewName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Account not found"));
    }

    @Test
    @DisplayName("Change name - Failed to update account")
    void testChangeName_Failure() throws Exception {
        when(accountService.get(testId)).thenReturn(testAccount);
        when(accountService.update(testAccount)).thenReturn(false);

        mockMvc.perform(patch("/api/accounts/{id}/name", testId)
                        .param("newName", testNewName))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Failed to update account"));
    }

    @Test
    @DisplayName("Delete account successful")
    void testDeleteAccount_Success() throws Exception {
        when(accountService.get(testId)).thenReturn(testAccount);
        when(accountService.delete(testId)).thenReturn(true);

        mockMvc.perform(delete("/api/accounts/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Account deleted successfully"));
    }

    @Test
    @DisplayName("Delete account - Account not found")
    void testDeleteAccount_NotFound() throws Exception {
        when(accountService.get(testId)).thenReturn(null);

        mockMvc.perform(delete("/api/accounts/{id}", testId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Account not found"));
    }

    @Test
    @DisplayName("Delete account - Failed to delete account")
    void testDeleteAccount_Failure() throws Exception {
        when(accountService.get(testId)).thenReturn(testAccount);
        when(accountService.delete(testId)).thenReturn(false);

        mockMvc.perform(delete("/api/accounts/{id}", testId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Failed to delete account"));
    }
}