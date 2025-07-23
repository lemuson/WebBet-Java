package bets.JPA.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Результаты")
public class ResultType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Результат")
    public Long id;

    @Column(name = "Название")
    public String name;
}