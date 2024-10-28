package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.envoi.enums.Roles;
import io.envoi.model.Account;
import io.envoi.model.dto.AccountDTO;
import io.envoi.service.AccountService;
import io.envoi.servlet.AuthServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class AuthServletTest {

    private AuthServlet authServlet;
    private AccountService accountService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        accountService = mock(AccountService.class);
        authServlet = new AuthServlet();
        authServlet.accountService = accountService;
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test login success.")
    public void testLoginSuccessful() throws Exception {
        String email = "admin@example.com";
        String password = "admin123";
        Account account = new Account(email, password, "admin", Roles.USER);
        AccountDTO accountDTO = new AccountDTO(1L, email, "admin", Roles.USER);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getPathInfo()).thenReturn("/login");
        when(req.getParameter("email")).thenReturn(email);
        when(req.getParameter("password")).thenReturn(password);
        when(accountService.emailExists(email)).thenReturn(true);
        when(accountService.getByEmail(email)).thenReturn(account);
        when(authServlet.accountMapper.toDTO(account)).thenReturn(accountDTO);

        authServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp.getWriter()).write(objectMapper.writeValueAsString(accountDTO));
    }

    @Test
    @DisplayName("Test login when email was not found.")
    public void testLoginEmailNotFound() throws Exception {
        String email = "unknown@example.com";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getPathInfo()).thenReturn("/login");
        when(req.getParameter("email")).thenReturn(email);
        when(accountService.emailExists(email)).thenReturn(false);

        authServlet.doGet(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Email doesn't exists.");
    }

    @Test
    @DisplayName("Test reg success.")
    public void testRegistrationSuccessful() throws Exception {
        String email = "new@example.com";
        String password = "newpass";
        String name = "newUser";
        Account account = new Account(email, password, name, Roles.USER);
        AccountDTO accountDTO = new AccountDTO(null, email, name, Roles.USER);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getPathInfo()).thenReturn("/registration");
        when(req.getParameter("email")).thenReturn(email);
        when(req.getParameter("password")).thenReturn(password);
        when(req.getParameter("nickname")).thenReturn(name);
        when(accountService.emailExists(email)).thenReturn(false);
        when(accountService.save(account)).thenReturn(true);
        when(accountService.getByEmail(email)).thenReturn(account);

        when(authServlet.accountMapper.toDTO(account)).thenReturn(accountDTO);

        authServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);
        verify(resp.getWriter()).write(objectMapper.writeValueAsString(accountDTO));
    }

    @Test
    @DisplayName("Test reg when email exists")
    public void testRegistrationEmailExists() throws Exception {
        String email = "existing@example.com";
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getPathInfo()).thenReturn("/registration");
        when(req.getParameter("email")).thenReturn(email);
        when(accountService.emailExists(email)).thenReturn(true);

        authServlet.doGet(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_CONFLICT, "Email already exists.");
    }
}