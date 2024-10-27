package io.envoi.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.annotations.Loggable;
import io.envoi.dao.AccountDAO;
import io.envoi.enums.Roles;
import io.envoi.mapper.AccountMapper;
import io.envoi.model.Account;
import io.envoi.model.dto.AccountDTO;
import io.envoi.service.AccountService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Loggable
@WebServlet(name="AuthServlet", urlPatterns = "/api/auth/*")
public class AuthServlet extends HttpServlet {
    public AccountService accountService = new AccountService(new AccountDAO());
    private final ObjectMapper objectMapper = new ObjectMapper();
    public AccountMapper accountMapper = AccountMapper.INSTANCE;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo();

        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        switch (action) {
            case "/registration" -> registration(req, resp);
            case "/login" -> login(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Action not found");
        }
    }

    private void registration(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //AccountDTO doesn't have password
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String nickname = req.getParameter("nickname");

        if (accountService.emailExists(email)) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Email already exists.");
            return;
        }

        Account account = new Account(email, password, nickname, Roles.USER);

        if(accountService.save(account)) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            account = accountService.getByEmail(email);
            AccountDTO accountDTO = accountMapper.toDTO(account);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(accountDTO));
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to create account. Please try again.");
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (!accountService.emailExists(email)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Email doesn't exists.");
            return;
        }

        Account account = accountService.getByEmail(email);

        if (account.getPassword().equals(password)) {
            AccountDTO accountDTO = accountMapper.toDTO(account);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(accountDTO));
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid password.");
        }
    }
}