package bets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ConnectJPA
{
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SportsBetsPU");

    public static EntityManager getEntityManager()
    {
        return emf.createEntityManager();
    }
}