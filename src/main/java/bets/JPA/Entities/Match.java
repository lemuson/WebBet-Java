package bets.JPA.Entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Матчи")
public class Match
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Матч")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Команда1")
    public Team team1;

    @ManyToOne
    @JoinColumn(name = "ID_Команда2")
    public Team team2;

    @Column(name = "ДатаМатча")
    public Date matchDate;

    @ManyToOne
    @JoinColumn(name = "ID_ВидСпорта")
    public SportType sportType;

    @Column(name = "Коэффициент_П1")
    public double odds_win1;

    @Column(name = "Коэффициент_П2")
    public double odds_win2;

    @Column(name = "Коэффициент_Ничья")
    public double odds_draw;

    @ManyToOne
    @JoinColumn(name = "ID_Результат")
    public ResultType resultType;
}