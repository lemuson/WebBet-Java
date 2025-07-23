package bets.JPA.EntityManagers;

import bets.JPA.Entities.Account;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

public class AccountEntityManager
{
    @PersistenceContext
    private final EntityManager em;

    public AccountEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public Account findByLoginPassword(String login, String password)
    {
        try
        {
            String jpql = "SELECT account FROM Account account WHERE account.username = :login AND account.password= :password";
            TypedQuery<Account> query = em.createQuery(jpql, Account.class);
            query.setParameter("login", login);
            query.setParameter("password", password);

            return query.getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Account findByID(int accountID)
    {
        String jpql = "SELECT account FROM Account account WHERE account.id = :accountID";
        TypedQuery<Account> query = em.createQuery(jpql, Account.class);
        query.setParameter("accountID", accountID);

        return query.getSingleResult();
    }

    @Transactional
    public void updateBalance(int accountID, double stake)
    {
        EntityTransaction transaction = em.getTransaction();
        try
        {
            transaction.begin();
            Account account = em.find(Account.class, accountID);
            if (account != null)
            {
                account.user.balance = account.user.balance + stake;
                em.merge(account);
            }
            transaction.commit();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void close()
    {
        if (em != null && em.isOpen())
            em.close();
    }
}