package io.envoi.controller;

import io.envoi.model.Account;
import io.envoi.service.AccountService;
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

    @PatchMapping(value = "/{id}/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeEmail(@PathVariable Long id, @RequestParam String newEmail) {
        return updateAccountField(id, account -> account.setEmail(newEmail), "Email updated successfully");
    }

    @PatchMapping(value = "/{id}/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeName(@PathVariable Long id, @RequestParam String newName) {
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