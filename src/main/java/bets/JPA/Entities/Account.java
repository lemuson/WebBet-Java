package bets.JPA.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Аккаунты")
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Аккаунт")
    public Long id;

    @Column(name = "Логин")
    public String username;

    @Column(name = "Пароль")
    public String password;

    @ManyToOne
    @JoinColumn(name = "ID_Пользователь")
    public User user;
}
