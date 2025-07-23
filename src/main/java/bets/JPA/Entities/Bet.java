package bets.JPA.Entities;

import jakarta.persistence.*;

import javax.xml.transform.Result;

@Entity
@Table(name = "Ставки")
public class Bet
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Ставка")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Аккаунт")
    public Account account;

    @ManyToOne
    @JoinColumn(name = "ID_Матч")
    public Match match;

    @Column(name = "Сумма")
    public Double amount;

    @Column(name = "Коэффициент")
    public Double odd;

    @Column(name = "Выигрыш")
    public Double win;

    @ManyToOne
    @JoinColumn(name = "ID_Статус")
    public Status status;

    @ManyToOne
    @JoinColumn(name = "ID_Результат")
    public ResultType result;
}