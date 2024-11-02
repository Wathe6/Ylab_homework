package io.envoi.controller;

import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.service.AccountService;
import io.envoi.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AccountService accountService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(
            @PathVariable String email,
            @PathVariable String password,
            @PathVariable String name) {

        if(accountService.emailExists(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        Account account = new Account(email, password, name, Roles.USER);
        if(accountService.save(account)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create account");
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        if(!Validation.isValidEmail(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email");
        }

        Account account = accountService.getByEmail(email);
        if (account == null || !account.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        return ResponseEntity.ok("Login successful");
    }
}