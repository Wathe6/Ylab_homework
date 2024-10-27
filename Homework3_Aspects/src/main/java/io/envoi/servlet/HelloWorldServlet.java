package io.envoi.servlet;

import io.envoi.annotations.Loggable;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Loggable
@WebServlet("/hello")
public class HelloWorldServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().write("<h1>Hello, World!</h1>");

        try
        {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5430/postgres", "postgres", "psswd200");
            if(connection.getSchema().equals("public")) {
                resp.getWriter().write("<h1>Database is running!</h1>");
            } else {
                resp.getWriter().write("<h1>Failed to launch database!</h1>");
            }
            connection.close();
        } catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}