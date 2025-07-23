package bets.JPA.EntityManagers;

import bets.JPA.Entities.Bet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BetEntityManager
{
    private final EntityManager em;

    public BetEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public List<Bet> findMatchesByAccount(int userID)
    {
        String jpql = "SELECT bet FROM Bet bet WHERE bet.account.id = :userID";
        TypedQuery<Bet> query = em.createQuery(jpql, Bet.class);
        query.setParameter("userID", userID);

        return query.getResultList();
    }

    public List<Bet> findBetByMatches(int matchID)
    {
        String jpql = "SELECT bet FROM Bet bet WHERE bet.match.id = :matchID";
        TypedQuery<Bet> query = em.createQuery(jpql, Bet.class);
        query.setParameter("matchID", matchID);

        return query.getResultList();
    }

    public void save(Bet bet)
    {
        EntityTransaction transaction = em.getTransaction();
        try
        {
            transaction.begin();
            if (bet.id == null)
                em.persist(bet);
            else
                em.merge(bet);

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction.isActive())
                transaction.rollback();
        }
    }

    public void close()
    {
        if (em != null && em.isOpen())
            em.close();
    }
}