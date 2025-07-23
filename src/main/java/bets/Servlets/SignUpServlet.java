package bets.Servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Objects;

@WebServlet(urlPatterns = "/register")
public class SignUpServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        resp.setContentType("text/html");
        resp.getWriter().println("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <link rel="stylesheet" href="Styles.css">
                    <title>Регистрация</title>
                </head>
                <body>
                    <header class="general-header">
                        <nav>
                            <a href="bets">Все события</a>
                            <a href="account">Аккаунт</a>
                        </nav>
                    </header>
                
                    <div class="authorization-container">
                        <h1>Регистрация</h1>
                        <form method='post' action='register'>
                            <div class="authorization-input">
                                <label for="name">Имя</label>
                                <input type="text" id="name" name="name" required>
                            </div>
                            <div class="authorization-input">
                                <label for="phone">Номер телефона</label>
                                <input type="tel" id="phone" name="phone" required>
                            </div>
                            <div class="authorization-input">
                                <label for="email">Почта</label>
                                <input type="email" id="email" name="email">
                            </div>
                            <div class="authorization-input">
                                <label for="promo">Промокод</label>
                                <input type="text" id="promo" name="promo">
                            </div>
                            <div class="authorization-input">
                                <label for="login">Логин</label>
                                <input type="text" id="login" name="login" required>
                            </div>
                            <div class="authorization-input">
                                <label for="password">Пароль</label>
                                <input type="password" id="password" name="password" required>
                            </div>
                            <button type="submit" class="main-button">Зарегистрироваться</button>
                        </form>
                        <div class="authorization-redirect">
                            <p>Уже есть аккаунт? <a href="login">Войти</a></p>
                        </div>
                    </div>
                </body>
                </html>
                """);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch (ClassNotFoundException e)
        {
            System.err.println(e.getMessage());
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;;encrypt=false;databaseName=SportsBets", "user", "0000"))
        {
            String userQuery = "INSERT INTO Пользователи (Имя, НомерТелефона, Почта, Баланс) VALUES (?, ?, ?, ?)";
            try (PreparedStatement userStmt = connection.prepareStatement(userQuery))
            {
                userStmt.setString(1, name);
                userStmt.setString(2, phone);
                userStmt.setString(3, email);
                userStmt.setBigDecimal(4, BigDecimal.valueOf(Objects.equals(req.getParameter("promo"), "get1000") ? 1000 : 0));
                userStmt.executeUpdate();
            }

            String getUserIdQuery = "SELECT ID_Пользователь FROM Пользователи WHERE НомерТелефона = ?";
            try (PreparedStatement getUserStmt = connection.prepareStatement(getUserIdQuery))
            {
                getUserStmt.setString(1, phone);
                try (var resultSet = getUserStmt.executeQuery())
                {
                    if (resultSet.next())
                    {
                        int userId = resultSet.getInt("ID_Пользователь");

                        String accountQuery = "INSERT INTO Аккаунты (Логин, Пароль, ID_Пользователь) VALUES (?, ?, ?)";
                        try (PreparedStatement accountStmt = connection.prepareStatement(accountQuery))
                        {
                            accountStmt.setString(1, login);
                            accountStmt.setString(2, password);
                            accountStmt.setInt(3, userId);
                            accountStmt.executeUpdate();
                        }
                    }
                }
            }
            resp.sendRedirect("login");
        }
        catch (Exception e)
        {
            resp.setContentType("text/html");
            resp.getWriter().println("Ошибка при регистрации. Пожалуйста, попробуйте позже." + e.getMessage());
        }
    }
}