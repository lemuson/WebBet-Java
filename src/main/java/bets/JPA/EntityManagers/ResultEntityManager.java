package bets.JPA.EntityManagers;

import bets.JPA.Entities.ResultType;
import bets.JPA.Entities.SportType;
import bets.JPA.Entities.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ResultEntityManager
{
    private final EntityManager em;

    public ResultEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public ResultType findResultByName(String nameResult)
    {
        String jpql = "SELECT result FROM ResultType result WHERE result.name = :nameResult";
        TypedQuery<ResultType> query = em.createQuery(jpql, ResultType.class);
        query.setParameter("nameResult", nameResult);

        return query.getSingleResult();
    }

    public Status getStatusByName(String name)
    {
        String jpql = "SELECT status FROM Status status WHERE status.name = :name";
        TypedQuery<Status> query = em.createQuery(jpql, Status.class);
        query.setParameter("name", name);

        return query.getResultList().get(0);
    }

    public void close()
    {
        if (em != null && em.isOpen())
            em.close();
    }
}