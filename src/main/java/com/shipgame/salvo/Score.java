package com.shipgame.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private Double score;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name="player")
    private Player player;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name="game")
    private Game game;

    public Score(){};

    public Score(Double score, Player player, Game game) {
        this.score = score;
        this.player = player;
        this.game = game;
        player.addScore(this);
        game.addScore(this);
    }

    //getters
    public Long getId() {
        return id;
    }

    public Double getScore() {
        return score;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }
    //setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
