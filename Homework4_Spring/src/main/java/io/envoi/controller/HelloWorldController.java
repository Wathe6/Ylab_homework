package io.envoi.controller;

import io.envoi.annotation.Loggable;
import io.envoi.config.LiquibaseConfig;
import io.envoi.dao.AccountDAO;
import io.envoi.enums.Roles;
import io.envoi.mapper.AccountMapper;
import io.envoi.mapper.HelloWorldMapper;
import io.envoi.model.Account;
import io.envoi.model.HelloWorld;
import io.envoi.model.dto.AccountDTO;
import io.envoi.model.dto.HelloWorldDTO;
import io.envoi.service.AccountService;
import io.envoi.service.HelloWorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;

@Loggable
@RestController
@RequiredArgsConstructor
public class HelloWorldController {
    private final HelloWorldMapper helloWorldMapper;
    private final HelloWorldService service;
    private final AccountMapper accountMapper = AccountMapper.INSTANCE;
    private final AccountService accountService;

    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HelloWorldDTO> sayHello() {
        HelloWorld helloWorld = service.sayHello();
        HelloWorldDTO helloWorldDTO = helloWorldMapper.modelToDTO(helloWorld);
        return ResponseEntity.ok(helloWorldDTO);
    }
    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDTO> getAccount() {
        Account account = accountService.get(1L);
        if(account == null) {
            return ResponseEntity.ok(new AccountDTO(0L,"Account is empty", "", Roles.USER));
        }
        AccountDTO accountDTO = accountMapper.toDTO(account);
        return ResponseEntity.ok(accountDTO);
    }
    @GetMapping(value = "/db", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkConnection() throws SQLException {
        Connection connection = LiquibaseConfig.getDbConnection();
        String flag;
        if(connection.getSchema().equals("habits_schema")) {
            flag = "Database is connected.";
        } else {
            flag = "Database is disconnected.";
        }
        return ResponseEntity.ok(flag);
    }
}