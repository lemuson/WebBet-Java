package bets.JPA.EntityManagers;

import bets.JPA.Entities.Match;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MatchEntityManager
{
    private final EntityManager em;

    public MatchEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public List<Match> findActiveMatchesBySportType(String sportTypeName)
    {
        String jpql = "SELECT m FROM Match m WHERE m.sportType.name = :sportTypeName and m.resultType.name = :state";
        TypedQuery<Match> query = em.createQuery(jpql, Match.class);
        query.setParameter("sportTypeName", sportTypeName);
        query.setParameter("state", "Ожидание");

        return query.getResultList();
    }

    public Match findMatchByID(int matchID)
    {
        String jpql = "SELECT match FROM Match match WHERE match.id = :matchID";
        TypedQuery<Match> query = em.createQuery(jpql, Match.class);
        query.setParameter("matchID", matchID);

        return query.getResultList().get(0);
    }

    public void save(Match match)
    {
        EntityTransaction transaction = em.getTransaction();
        try
        {
            transaction.begin();
            if (match.id == null)
                em.persist(match);
            else
                em.merge(match);

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