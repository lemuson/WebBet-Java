package bets.JPA.EntityManagers;

import bets.JPA.Entities.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class StatusEntityManager
{
    private final EntityManager em;

    public StatusEntityManager(EntityManager em)
    {
        this.em = em;
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