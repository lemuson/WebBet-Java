package bets.JPA.EntityManagers;

import bets.JPA.Entities.ResultType;
import bets.JPA.Entities.SportType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SportsEntityManager
{
    private final EntityManager em;

    public SportsEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public List<SportType> getSports()
    {
        String jpql = "SELECT sport FROM SportType sport";
        TypedQuery<SportType> query = em.createQuery(jpql, SportType.class);

        return query.getResultList();
    }

    public ResultType findResultByName(String nameResult)
    {
        String jpql = "SELECT result FROM ResultType result WHERE result.name = :nameResult";
        TypedQuery<ResultType> query = em.createQuery(jpql, ResultType.class);
        query.setParameter("nameResult", nameResult);

        return query.getSingleResult();
    }

    public void close()
    {
        if (em != null && em.isOpen())
            em.close();
    }
}