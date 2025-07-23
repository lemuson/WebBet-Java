package bets.JPA.EntityManagers;

import bets.JPA.Entities.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TeamEntityManager
{
    private final EntityManager em;

    public TeamEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public List<Team> getTeams()
    {
        String jpql = "SELECT team FROM Team team";
        TypedQuery<Team> query = em.createQuery(jpql, Team.class);

        return query.getResultList();
    }

    public void close()
    {
        if (em != null && em.isOpen())
            em.close();
    }
}