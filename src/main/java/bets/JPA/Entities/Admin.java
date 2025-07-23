package bets.JPA.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Администраторы")
public class Admin
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Администратор")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Аккаунт")
    public Account account;
}
