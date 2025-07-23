package bets;

import bets.JPA.Entities.Bet;
import bets.JPA.Entities.Match;
import bets.JPA.Entities.Status;
import bets.JPA.EntityManagers.BetEntityManager;
import bets.JPA.EntityManagers.MatchEntityManager;
import bets.JPA.EntityManagers.ResultEntityManager;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Random;

public class PlayMatch
{
    private static final Random random = new Random();

    public static void Start(Match match)
    {
        EntityManager em = ConnectJPA.getEntityManager();
        MatchEntityManager matchSaver = new MatchEntityManager(em);
        BetEntityManager betsSaver = new BetEntityManager(em);
        ResultEntityManager resultSearcher = new ResultEntityManager(em);

        String[] outcomes = {"П1", "Н", "П2"};
        String result = outcomes[random.nextInt(outcomes.length)];
        match.resultType = resultSearcher.findResultByName(result);

        matchSaver.save(match);

        Status statusWin = resultSearcher.getStatusByName("Выигрыш");
        Status statusLose = resultSearcher.getStatusByName("Проигрыш");

        //Обновляем ставки
        List<Bet> bets = betsSaver.findBetByMatches(Math.toIntExact(match.id));
        for (Bet bet : bets)
        {
            if (bet.match.id.equals(match.id))
            {
                if ("П1".equals(result) &&  "П1".equals(bet.result.name))
                {
                    bet.account.user.balance += bet.amount * bet.odd;
                    bet.status = statusWin;
                }
                else if ("П2".equals(result) && "П2".equals(bet.result.name))
                {
                    bet.account.user.balance += bet.amount * bet.odd;
                    bet.status = statusWin;
                }
                else if ("Н".equals(result) && "Н".equals(bet.result.name))
                {
                    bet.account.user.balance += bet.amount * bet.odd;
                    bet.status = statusWin;
                }
                else
                {
                    bet.status = statusLose;
                }
                betsSaver.save(bet);
            }
        }
    }
}
