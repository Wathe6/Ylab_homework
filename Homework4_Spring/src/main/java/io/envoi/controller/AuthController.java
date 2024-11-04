package io.envoi.controller;

import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.service.AccountService;
import io.envoi.util.Validation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Register and login controller.
 * */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AccountService accountService;

    @Operation(summary = "Register new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response"),
            @ApiResponse(responseCode = "401", description = "Invalid email"),
            @ApiResponse(responseCode = "401", description = "Wrong email or password"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(
            @Parameter(name = "email", required = true) @PathVariable String email,
            @Parameter(name = "password", required = true) @PathVariable String password,
            @Parameter(name = "name", required = true) @PathVariable String name) {

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

    @Operation(summary = "Register new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response"),
            @ApiResponse(responseCode = "401", description = "Invalid email"),
            @ApiResponse(responseCode = "401", description = "Wrong email or password")
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(
            @Parameter(name = "email", required = true) @RequestParam String email,
            @Parameter(name = "password", required = true) @RequestParam String password) {

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