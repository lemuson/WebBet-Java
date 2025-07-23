package bets.Servlets;

import bets.ConnectJPA;
import bets.Converter;
import bets.JPA.Entities.Account;
import bets.JPA.Entities.Match;
import bets.JPA.EntityManagers.AccountEntityManager;
import bets.JPA.EntityManagers.AdminsEntityManager;
import bets.JPA.EntityManagers.MatchEntityManager;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/bets")
public class BetsServlet extends HttpServlet
{
    private MatchEntityManager matchFinder;
    private AccountEntityManager accountFinder;
    private AdminsEntityManager adminFinder;
    @Override
    public void init()
    {
        try
        {
            EntityManager em = ConnectJPA.getEntityManager();
            matchFinder = new MatchEntityManager(em);
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
        String sport = req.getParameter("sport");
        if (sport == null)
            sport = "Футбол";

        HttpSession session = req.getSession();
        Integer userID = (Integer) session.getAttribute("userId");
        Account account = null;
        if (userID != null)
        {
            account = accountFinder.findByID(userID);
            session.setAttribute("userId", Integer.parseInt(account.id.toString()));
        }

        resp.setContentType("text/html");
        resp.getWriter().println("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <link rel="stylesheet" href="Styles.css">
                        <title>Все события</title>
                    </head>
                    <body>
                        <header class="general-header">
                            <nav>
                                <a href="bets">Все события</a>
                                <a href="account">Аккаунт</a>
                            </nav>
                    """);
        if(account != null && adminFinder.findByAccountID(userID) == null)
            resp.getWriter().println("""
                        <div class="general-balance-container">
                            <span class="general-balance">Баланс: %s ₽</span>
                            <a class="general-balance-add">Пополнить</a>
                            <a class="general-balance-out">Вывод</a>
                        </div>
                        """.formatted(account.user.balance));
        resp.getWriter().println("""
            </header>

            <!-- Отображение ставки -->
            <div class="bets-container">
            <nav class="bets-menu">
                <nav class="bets-menu">
                    <a href="bets?sport=Футбол" class="main-button">Футбол</a>
                    <a href="bets?sport=Баскетбол" class="main-button">Баскетбол</a>
                    <a href="bets?sport=Теннис" class="main-button">Теннис</a>
                    <a href="bets?sport=Хоккей" class="main-button">Хоккей</a>
                    <a href="bets?sport=Гольф" class="main-button">Гольф</a>
                    <a href="bets?sport=Регби" class="main-button">Регби</a>
                    <a href="bets?sport=Бобслей" class="main-button">Бобслей</a>
                    <a href="bets?sport=Фрисби" class="main-button">Фрисби</a>
                    <a href="bets?sport=Туризм" class="main-button">Туризм</a>
                </nav>
            </nav>
        
            <div class="bets-info-block">
        """);

        List<Match> currentMatches = matchFinder.findActiveMatchesBySportType(sport);
        for(Match match : currentMatches)
        {
            resp.getWriter().println("""
            <div class="bets-match-container">
                <div class="bets-match-header">
                  <span>%s</span>
                </div>
        
                <div class="bets-teams">
                  <div class="bets-team">
                    <img src="data:image/png;base64,%s" alt="Логотип 1" class="bets-team-logo">
                    <span class="bets-team-name">%s</span>
                  </div>
        
                  <div class="bets-team">
                    <img src="data:image/png;base64,%s" alt="Логотип 2" class="bets-team-logo">
                    <span class="bets-team-name">%s</span>
                  </div>
                </div>
        
                <div class="bets-coefficients">
                  <div class="bets-coefficients-layout">
                    <span>П1</span>
                    <span>Н</span>
                    <span>П2</span>
                  </div>
        
                  <div class="bets-coefficients-layout">
                    <a href="place-bet?matchId=%d&betType=win1" class="main-button">%s</a>
                    <a href="place-bet?matchId=%d&betType=draw" class="main-button">%s</a>
                    <a href="place-bet?matchId=%d&betType=win2" class="main-button">%s</a>
                  </div>
                </div>
            </div>
            """.formatted(
                    match.matchDate.toString(),
                    Converter.convertToBase64(match.team1.logo),
                    match.team1.name,
                    Converter.convertToBase64(match.team2.logo),
                    match.team2.name,
                    match.id, match.odds_win1,
                    match.id, match.odds_draw,
                    match.id, match.odds_win2));
        }

        resp.getWriter().println("""
            </div>
          </div>
        </body>
        </html>
        """);
    }

    @Override
    public void destroy()
    {
        matchFinder.close();
        accountFinder.close();
        adminFinder.close();
    }
}