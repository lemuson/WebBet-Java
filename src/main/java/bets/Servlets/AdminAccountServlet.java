package bets.Servlets;

import bets.ConnectJPA;
import bets.JPA.Entities.Match;
import bets.JPA.Entities.SportType;
import bets.JPA.Entities.Team;
import bets.JPA.EntityManagers.AdminsEntityManager;
import bets.JPA.EntityManagers.SportsEntityManager;
import bets.JPA.EntityManagers.TeamEntityManager;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class AdminAccountServlet extends HttpServlet
{
    private SportsEntityManager sportFinder;
    private TeamEntityManager teamsFinder;
    private AdminsEntityManager adminsFinder;

    @Override
    public void init()
    {
        try
        {
            EntityManager em = ConnectJPA.getEntityManager();
            sportFinder = new SportsEntityManager(em);
            teamsFinder = new TeamEntityManager(em);
            adminsFinder = new AdminsEntityManager(em);
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
        else if(adminsFinder.findByAccountID(userID) != null)
        {
            List<Team> teams = teamsFinder.getTeams();
            List<SportType> sports = sportFinder.getSports();

            resp.setContentType("text/html");
            resp.getWriter().println("""
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <link rel="stylesheet" href="Styles.css">
                            <title>Администратор</title>
                        </head>
                        <body>
                            <header class="general-header">
                                <nav>
                                    <a href="bets">Все события</a>
                                    <a href="account">Аккаунт</a>
                                </nav>
                            </header>

                            <div class="admin-container">
                                <!-- Блок для команд -->
                                <form method="POST" action="" enctype="multipart/form-data">
                                    <div class="admin-block">
                                        <h2>Добавить команду</h2>
                                        <div class="admin-input">
                                            <label for="team-name">Название команды</label>
                                            <input type="text" id="team-name" name="team-name">
                                        </div>
                                        <div class="admin-input">
                                            <label for="team-logo">Логотип команды</label>
                                            <input type="file" id="team-logo" name="team-logo" accept="image/*">
                                        </div>
                                        <button type="submit" name="action" value="save-team" class="main-button admin-save-button">Сохранить команду</button>
                                    </div>
                                </form>
                    
                                <!-- Блок для видов спорта -->
                                <form method="POST" action="">
                                    <div class="admin-block">
                                        <h2>Добавить вид спорта</h2>
                                        <div class="admin-input">
                                            <label for="sport-name">Название вида спорта</label>
                                            <input type="text" id="sport-name" name="sport-name">
                                        </div>
                                        <button type="submit" name="action" value="save-sport" class="main-button admin-save-button">Сохранить вид спорта</button>
                                    </div>
                                </form>
                    
                                <!-- Блок для матчей -->
                                <form method="POST" action="">
                                    <div class="admin-block">
                                        <h2>Добавить матч</h2>
                                        <div class="admin-input">
                                            <label for="sport-id">Вид спорта</label>
                                            <select id="sport-id" name="sport-id">
                                                <option value="" disabled selected>Выберите вид спорта</option>
                                                <!-- Динамическое добавление видов спорта в select -->
                                                %s
                                            </select>
                                        </div>
                                        <div class="admin-input">
                                            <label for="team1-id">Команда 1</label>
                                            <select id="team1-id" name="team1-id">
                                                <option value="" disabled selected>Выберите команду</option>
                                                <!-- Динамическое добавление команд в select -->
                                                %s
                                            </select>
                                        </div>
                                        <div class="admin-input">
                                            <label for="team2-id">Команда 2</label>
                                            <select id="team2-id" name="team2-id">
                                                <option value="" disabled selected>Выберите команду</option>
                                                <!-- Динамическое добавление команд в select -->
                                                %s
                                            </select>
                                        </div>
                                        <div class="admin-input">
                                            <label for="match-date">Дата матча</label>
                                            <input type="datetime-local" id="match-date" name="match-date">
                                        </div>
                                        <div class="admin-input">
                                            <label for="coef1">Коэффициент П1</label>
                                            <input type="number" step="0.01" id="coef1" name="coef1">
                                        </div>
                                        <div class="admin-input">
                                            <label for="coef2">Коэффициент П2</label>
                                            <input type="number" step="0.01" id="coef2" name="coef2">
                                        </div>
                                        <div class="admin-input">
                                            <label for="coef-draw">Коэффициент Ничья</label>
                                            <input type="number" step="0.01" id="coef-draw" name="coef-draw">
                                        </div>
                                        <button type="submit" name="action" value="save-match" class="main-button admin-save-button">Сохранить матч</button>
                                        <a href="logout">
                                            <button class="main-button" type="button" id="account-logout">Выйти</button>
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </body>
                        </html>
                    """.formatted(
                    sports.stream().map(sport ->
                            String.format("<option value=\"%d\">%s</option>", sport.id, sport.name)
                    ).collect(Collectors.joining("\n")),

                    teams.stream().map(team ->
                            String.format("<option value=\"%d\">%s</option>", team.id, team.name)
                    ).collect(Collectors.joining("\n")),

                    teams.stream().map(team ->
                            String.format("<option value=\"%d\">%s</option>", team.id, team.name)
                    ).collect(Collectors.joining("\n"))
            ));
        }
        else
            resp.sendRedirect("account");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        try
        {
            String action = req.getParameter("action");
            EntityManager em = ConnectJPA.getEntityManager();
            em.getTransaction().begin();

            if ("save-team".equals(action))
            {
                String teamName = req.getParameter("team-name");
                Part logoPart = req.getPart("team-logo");
                byte[] logoData = null;

                if (logoPart != null)
                {
                    logoData = new byte[(int) logoPart.getSize()];
                    logoPart.getInputStream().read(logoData);
                }

                Team team = new Team();
                team.name = teamName;
                team.logo = logoData;
                em.persist(team);
            }
            else if ("save-sport".equals(action))
            {
                String sportName = req.getParameter("sport-name");

                SportType sport = new SportType();
                sport.name = sportName;
                em.persist(sport);
            }
            else if ("save-match".equals(action))
            {
                String sportId = req.getParameter("sport-id");
                String team1Id = req.getParameter("team1-id");
                String team2Id = req.getParameter("team2-id");

                Match match = new Match();
                match.sportType = em.find(SportType.class, Integer.parseInt(sportId));
                match.team1 = em.find(Team.class, Integer.parseInt(team1Id));
                match.team2 = em.find(Team.class, Integer.parseInt(team2Id));
                match.matchDate = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("match-date"));
                match.odds_win1 = Double.parseDouble(req.getParameter("coef1"));
                match.odds_win2 = Double.parseDouble(req.getParameter("coef2"));
                match.odds_draw = Double.parseDouble(req.getParameter("coef-draw"));
                match.resultType = sportFinder.findResultByName("Ожидание");
                em.persist(match);
            }
            em.getTransaction().commit();
            em.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        resp.sendRedirect("admin");
    }

    @Override
    public void destroy()
    {
        sportFinder.close();
        teamsFinder.close();
        adminsFinder.close();
    }
}