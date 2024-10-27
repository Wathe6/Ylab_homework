package io.envoi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.annotations.Loggable;
import io.envoi.dao.AccountDAO;
import io.envoi.mapper.AccountMapper;
import io.envoi.model.Account;
import io.envoi.model.dto.AccountDTO;
import io.envoi.service.AccountService;
import io.envoi.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Loggable
@WebServlet(name="AccountServlet", urlPatterns = "/api/account/*")
public class AccountServlet extends HttpServlet {
    private final AccountService accountService = new AccountService(new AccountDAO());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AccountMapper accountMapper = AccountMapper.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo();

        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        switch (action) {
            case "/changeEmail" -> changeEmail(req, resp);
            case "/changeNickname" -> changeNickname(req, resp);
            case "/deleteAccount" -> deleteAccount(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Action not found");
        }
    }

    private void changeEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AccountDTO accountDTO = objectMapper.readValue(req.getReader(), AccountDTO.class);
        Account account = accountService.get(accountDTO.id());

        if (account == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Account not found");
            return;
        }

        if (!Validation.isValidEmail(accountDTO.email())) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email");
            return;
        }

        account.setEmail(accountDTO.email());
        if (accountService.update(account)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Email updated successfully\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating email");
        }
    }


    private void changeNickname(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AccountDTO accountDTO = objectMapper.readValue(req.getReader(), AccountDTO.class);
        Account account = accountService.get(accountDTO.id());

        if (account == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Account not found");
            return;
        }

        account.setName(accountDTO.name());
        if (accountService.update(account)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Nickname changed successfully\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error changing nickname");
        }
    }

    private void deleteAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getReader().readLine());

        if (accountService.delete(id)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Account deleted successfully\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting account");
        }
    }
}