package bets.Servlets;

import bets.ConnectJPA;
import bets.Converter;
import bets.JPA.Entities.Account;
import bets.JPA.Entities.Bet;
import bets.JPA.Entities.Match;
import bets.JPA.Entities.ResultType;
import bets.JPA.EntityManagers.*;
import bets.PlayMatch;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@WebServlet(urlPatterns = "/place-bet")
public class PlaceBetServlet extends HttpServlet
{
    private MatchEntityManager matchFinder;
    private AccountEntityManager accountFinder;
    private StatusEntityManager statusFinder;
    private AdminsEntityManager adminFinder;
    private ResultEntityManager resultFinder;

    @Override
    public void init()
    {
        try
        {
            EntityManager em = ConnectJPA.getEntityManager();
            matchFinder = new MatchEntityManager(em);
            accountFinder = new AccountEntityManager(em);
            statusFinder = new StatusEntityManager(em);
            adminFinder = new AdminsEntityManager(em);
            resultFinder = new ResultEntityManager(em);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession session = req.getSession();
        Integer userID = (Integer) session.getAttribute("userId");
        if (userID == null)
            resp.sendRedirect("login");
        else
        {
            Account account = accountFinder.findByID(userID);
            Match match = matchFinder.findMatchByID(Integer.parseInt(req.getParameter("matchId")));
            resp.setContentType("text/html");
            resp.setContentType("text/html");
            resp.getWriter().println("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <link rel="stylesheet" href="Styles.css">
                        <title>Сделать ставку</title>
                    </head>
                    <body>
                        <header class="general-header">
                            <nav>
                                <a href="bets">Все события</a>
                                <a href="account">Аккаунт</a>
                            </nav>
                    """);
            if (account != null && adminFinder.findByAccountID(userID) == null)
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
                        <div class="place-bet-container">
                            <h2>Ставка на матч №%s</h2>
                            <div class="place-bet-match-info">
                                Дата начала матча: %s
                            </div>
                   
                            <div class="place-bet-teams">
                                <div class="place-bet-team">
                                    <img src="data:image/png;base64,%s" alt="Логотип команды 1">
                                    <div>%s</div>
                                </div>
                                <div>VS</div>
                                <div class="place-bet-team">
                                    <img src="data:image/png;base64,%s" alt="Логотип команды 2">
                                    <div>%s</div>
                                </div>
                            </div>
                    """.formatted(match.id, match.matchDate, Converter.convertToBase64(match.team1.logo), match.team1.name, Converter.convertToBase64(match.team2.logo), match.team2.name));

            if(adminFinder.findByAccountID(userID) == null)
                resp.getWriter().println("""
                                <div class="place-bet-odds">
                                    <div class="place-bet-odds-labels">
                                        <span>Победа 1</span>
                                        <span>Ничья</span>
                                        <span>Победа 2</span>
                                    </div>
                                    <div class="place-bet-odds-buttons">
                                        <button class="place-bet-odds-btn" data-odd="%s" data-result-type="П1">%s</button>
                                        <button class="place-bet-odds-btn" data-odd="%s" data-result-type="Н">%s</button>
                                        <button class="place-bet-odds-btn" data-odd="%s" data-result-type="П2">%s</button>
                                    </div>
                                </div>
    
                                <form class="place-bet-form" action="place-bet" method="POST">
                                   <input type="hidden" name="matchId" id="matchId" value="%s">
                                   <input type="hidden" name="odds" id="odds">
                                   <input type="hidden" name="action" value="placeBet">
                                   <input type="hidden" name="resultType" id="result-type">
                                   <div class="place-bet-stake-input">
                                       <label for="stake-input">Введите сумму ставки:</label>
                                       <input type="number" name="stake" id="stake-input" placeholder="Введите сумму ставки" required>
                                   </div>
                                   <button type="submit" class="main-button">Сделать ставку</button>
                               </form>
                               
                               <div class="place-bet-expected-win" id="expected-win">
                                    Ожидаемый выигрыш: 0
                               </div>
                               """.formatted(match.odds_win1, match.odds_win1, match.odds_draw, match.odds_draw, match.odds_win2, match.odds_win2, match.id));
            else
            {
                resp.getWriter().println("""
                               <form class="place-bet-form" action="place-bet" method="POST">
                                   <input type="hidden" name="matchId" id="matchId" value="%s">
                                   <button type="submit" class="main-button">Начать матч</button>
                               </form>
                               """.formatted(match.id));
            }

            resp.getWriter().println("""
                        </div>
                    
                    <script>
                            document.addEventListener('DOMContentLoaded', () => {
                                 const oddsButtons = document.querySelectorAll('.place-bet-odds-btn');
                                 const stakeInput = document.getElementById('stake-input');
                                 const expectedWinElement = document.getElementById('expected-win');
                                 const oddsInput = document.getElementById('odds');
                                 const resultTypeInput = document.getElementById('result-type');
                    
                                 let selectedOdd = null;
                    
                    
                                 oddsButtons.forEach(button => {
                                     button.addEventListener('click', () => {
                                         oddsButtons.forEach(btn => btn.classList.remove('active'));
                                         button.classList.add('active');
                    
                                         selectedOdd = parseFloat(button.dataset.odd);
                                         oddsInput.value = selectedOdd;
                                         resultTypeInput.value = button.dataset.resultType;
                    
                                         updateExpectedWin();
                                     });
                                 });
                    
                                 stakeInput.addEventListener('input', updateExpectedWin);

                                 function updateExpectedWin() {
                                     const stake = parseFloat(stakeInput.value) || 0; // Получение введенной суммы ставки
                                     const expectedWin = selectedOdd ? (stake * selectedOdd).toFixed(2) : 0; // Расчет ожидаемого выигрыша
                                     expectedWinElement.textContent = `Ожидаемый выигрыш: ${expectedWin}`; // Обновление текста на странице
                                 }
                    
                                 // Проверка отправки формы
                                 const form = document.querySelector('.place-bet-form');
                                 form.addEventListener('submit', (event) => {
                                     if (!selectedOdd || !resultTypeInput.value) {
                                         event.preventDefault(); // Отмена отправки формы, если данные не заполнены
                                         alert('Пожалуйста, выберите результат и введите сумму ставки.');
                                     }
                                 });
                             });
                        </script>
                    </body>
                    </html>
                    """);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession session = req.getSession();
        Integer userID = (Integer) session.getAttribute("userId");
        Account account = accountFinder.findByID(userID);
        String action = req.getParameter("action");
        String resultType = req.getParameter("resultType");
        int matchID = Integer.parseInt(req.getParameter("matchId"));

        try
        {
            if("placeBet".equals(action))
            {
                Double stakeAmount = Double.parseDouble(req.getParameter("stake"));
                Double selectedOdd = Double.parseDouble(req.getParameter("odds"));
                System.out.println(resultType);

                if(account.user.balance >= stakeAmount)
                {
                    EntityManager em = ConnectJPA.getEntityManager();
                    em.getTransaction().begin();

                    Bet bet = new Bet();
                    bet.account = account;
                    bet.match = matchFinder.findMatchByID(matchID);
                    bet.amount = stakeAmount;
                    bet.odd = selectedOdd;
                    bet.win = stakeAmount * selectedOdd;
                    bet.status = statusFinder.getStatusByName("Открыта");
                    bet.result = resultFinder.findResultByName(resultType);
                    accountFinder.updateBalance(userID, -stakeAmount);

                    session.setAttribute("userId", Integer.parseInt(account.id.toString()));

                    em.persist(bet);
                    em.getTransaction().commit();
                    em.close();
                }
            }
            else
            {
                PlayMatch.Start(matchFinder.findMatchByID(matchID));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        resp.sendRedirect("bets");
    }
}