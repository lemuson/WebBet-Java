package bets.Servlets;

import bets.ConnectJPA;
import bets.Converter;
import bets.JPA.Entities.Account;
import bets.JPA.Entities.Bet;
import bets.JPA.EntityManagers.AccountEntityManager;
import bets.JPA.EntityManagers.AdminsEntityManager;
import bets.JPA.EntityManagers.BetEntityManager;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/account")
public class AccountServlet extends HttpServlet
{
    private BetEntityManager betFinder;
    private AccountEntityManager accountFinder;
    private AdminsEntityManager adminFinder;

    @Override
    public void init()
    {
        try
        {
            EntityManager em = ConnectJPA.getEntityManager();
            betFinder = new BetEntityManager(em);
            accountFinder = new AccountEntityManager(em);
            adminFinder = new AdminsEntityManager(em);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        HttpSession session = req.getSession();
        Integer userID = (Integer) session.getAttribute("userId");
        if (userID == null)
        {
            resp.sendRedirect("login");
        }
        else if(adminFinder.findByAccountID(userID) == null)
        {
            Account account = accountFinder.findByID(userID);
            session.setAttribute("userId", Integer.parseInt(account.id.toString()));

            resp.getWriter().println("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <link rel="stylesheet" href="Styles.css">
                        <title>Аккаунт</title>
                    </head>
                    <body>
                        <header class="general-header">
                            <nav>
                                <a href="bets">Все события</a>
                                <a href="account">Аккаунт</a>
                            </nav>
                    """);
            resp.getWriter().println("""
                    <div class="general-balance-container">
                        <span class="general-balance">Баланс: %s ₽</span>
                        <a class="general-balance-add">Пополнить</a>
                        <a class="general-balance-out">Вывод</a>
                    </div>
                    """.formatted(account.user.balance));
            resp.getWriter().println("""
                        </header>
                    
                        <div class="account-container">
                            <!-- Данные профиля -->
                            <div class="account-profile-container">
                                <h2>Мой профиль</h2>
                                <form>
                                    <div class="account-profile-info">
                                        <label for="login">Логин:</label>
                                        <input type="text" id="login" value="%s" disabled>
                                    </div>
                                    <div class="account-profile-info">
                                        <label for="name">Имя:</label>
                                        <input type="text" id="name" value="%s">
                                    </div>
                                    <div class="account-profile-info">
                                        <label for="phone">Номер телефона:</label>
                                        <input type="tel" id="phone" value="%s">
                                    </div>
                                    <div class="account-profile-info">
                                        <label for="email">Почта:</label>
                                        <input type="email" id="email" value="%s">
                                    </div>
                                    <!-- Кнопка сохранения -->
                                    <a href="updateAccount">
                                        <button class="main-button" type="submit" id="account-save">Сохранить</button>
                                    </a>
                                    <!-- Кнопка выхода -->
                                    <a href="logout">
                                        <button class="main-button" type="button" id="account-logout">Выйти</button>
                                    </a>
                                </form>
                            </div>
                    """.formatted(account.username, account.user.name, account.user.phoneNumber, account.user.email));

            resp.getWriter().println("""
                            <!-- Таблица ставок -->
                            <div class="account-history-container">
                                <h2>История ставок</h2>
                                <table class="account-bet-table">
                                    <thead>
                                        <tr>
                                            <th>Команда 1</th>
                                            <th>Ставка</th>
                                            <th>Коэффициент</th>
                                            <th>Команда 2</th>
                                            <th>Прогноз</th>
                                            <th>Статус</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                    """);

            List<Bet> currentBets = betFinder.findMatchesByAccount(userID);
            for(Bet bet : currentBets)
            {
                resp.getWriter().println("""
                    <tr>
                        <td>
                            <div class="account-team">
                                <img src="data:image/png;base64,%s" alt="Команда 1" class="account-team-logo">
                                <span class="account-team-name">%s</span>
                            </div>
                        </td>
                        <td>
                            <div class="account-price">%s₽</div>
                        </td>
                        <td>
                            <div class="account-coefficient">%s</div>
                        </td>
                        <td>
                            <div class="account-team">
                                <img src="data:image/png;base64,%s" alt="Команда 2" class="account-team-logo">
                                <span class="account-team-name">%s</span>
                            </div>
                        </td>
                        <td>
                            <div class="account-status">%s</div>
                        </td>
                        <td>
                            <div class="account-status">%s</div>
                        </td>
                    </tr>
                    """.formatted(Converter.convertToBase64(bet.match.team1.logo), bet.match.team1.name, bet.amount, bet.odd, Converter.convertToBase64(bet.match.team2.logo), bet.match.team2.name, bet.result.name ,bet.status.name));
            }

            resp.getWriter().println("""
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </body>
                    </html>
                    """);
        }
        else
        {
            resp.sendRedirect("admin");
        }
    }
}