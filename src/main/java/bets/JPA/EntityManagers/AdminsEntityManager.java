package bets.JPA.EntityManagers;

import bets.JPA.Entities.Admin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class AdminsEntityManager
{
    private final EntityManager em;

    public AdminsEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public Admin findByAccountID(int accountID)
    {
        try
        {
            String jpql = "SELECT admin FROM Admin admin WHERE admin.account.id = :accountID";
            TypedQuery<Admin> query = em.createQuery(jpql, Admin.class);
            query.setParameter("accountID", accountID);

            return query.getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public void close()
    {
        if (em != null && em.isOpen())
            em.close();
    }
}