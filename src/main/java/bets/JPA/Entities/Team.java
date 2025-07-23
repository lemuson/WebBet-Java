package bets.JPA.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Команды")
public class Team
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Команда")
    public Long id;

    @Column(name = "Название")
    public String name;

    @Lob
    @Column(name = "Логотип")
    public byte[] logo;
}
