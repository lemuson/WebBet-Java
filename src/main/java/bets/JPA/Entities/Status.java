package bets.JPA.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Статусы")
public class Status
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Статус")
    public Long id;

    @Column(name = "Название")
    public String name;
}