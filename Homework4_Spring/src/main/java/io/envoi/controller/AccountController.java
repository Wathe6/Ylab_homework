package io.envoi.controller;

import io.envoi.model.Account;
import io.envoi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Consumer;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    @Operation(summary = "Change e-mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping(value = "/{id}/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeEmail(
            @Parameter(name = "id", required = true) @PathVariable Long id,
            @Parameter(name = "newEmail", required = true) @RequestParam String newEmail) {
        return updateAccountField(id, account -> account.setEmail(newEmail), "Email updated successfully");
    }
    @Operation(summary = "Change name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Name updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Failed to update account")
    })
    @PatchMapping(value = "/{id}/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeName(
            @Parameter(name = "id", required = true) @PathVariable Long id,
            @Parameter(name = "newName", required = true) @RequestParam String newName) {
        return updateAccountField(id, account -> account.setName(newName), "Name updated successfully");
    }

    private ResponseEntity<String> updateAccountField(Long id, Consumer<Account> updateFunction, String successMessage) {
        Account account = accountService.get(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        updateFunction.accept(account);
        if (accountService.update(account)) {
            return ResponseEntity.ok(successMessage);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update account");
        }
    }

    @Operation(summary = "Delete account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Failed to delete account")
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Account account = accountService.get(id);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        if (accountService.delete(id)) {
            return ResponseEntity.ok("Account deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete account");
        }
    }
}