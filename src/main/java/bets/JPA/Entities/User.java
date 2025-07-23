package bets.JPA.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Пользователи")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Пользователь")
    public Long id;

    @Column(name = "Имя")
    public String name;

    @Column(name = "НомерТелефона")
    public String phoneNumber;

    @Column(name = "Почта")
    public String email;

    @Column(name = "Баланс")
    public Double balance;
}
