package bets.JPA.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ВидыСпорта")
public class SportType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ВидСпорта")
    public Long id;

    @Column(name = "Название", nullable = false, length = 50)
    public String name;
}
