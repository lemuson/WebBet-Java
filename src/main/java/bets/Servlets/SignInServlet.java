package bets.Servlets;

import bets.ConnectJPA;
import bets.JPA.Entities.Account;
import bets.JPA.EntityManagers.AccountEntityManager;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = "/login")
public class SignInServlet extends HttpServlet
{
    private AccountEntityManager accountFinder;

    @Override
    public void init()
    {
        try
        {
            EntityManager em = ConnectJPA.getEntityManager();
            accountFinder = new AccountEntityManager(em);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

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
            <title>Вход в аккаунт</title>
        </head>
        <body>
            <header class="general-header">
                <nav>
                    <a href="bets">Все события</a>
                    <a href="account">Аккаунт</a>
                </nav>
            </header>
        
            <div class="authorization-container">
                <h1>Вход в аккаунт</h1>
                <form method='post' action='login'>
                    <div class="authorization-input">
                        <label for="login">Логин</label>
                        <input type="text" id="login" name="login" required>
                    </div>
                    <div class="authorization-input">
                        <label for="password">Пароль</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <button type="submit" class="main-button">Войти</button>
                </form>
                <div class="authorization-redirect">
                    <p>Нет аккаунта? <a href="register">Зарегистрироваться</a></p>
                </div>
            </div>
        </body>
        </html>
        """);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        resp.setContentType("text/html");
        Account account = accountFinder.findByLoginPassword(login, password);
        if(account != null)
        {
            HttpSession session = req.getSession();
            session.setAttribute("userId", Integer.parseInt(account.id.toString()));
            resp.sendRedirect("account");
        }
        else
        {
            resp.sendRedirect("login");
        }
    }
}

//try
//{
//    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//}
//catch (ClassNotFoundException e)
//{
//    System.err.println(e.getMessage());
//}