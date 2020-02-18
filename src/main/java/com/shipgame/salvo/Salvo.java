package com.shipgame.salvo;

import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "salvo")
    private GamePlayer gamePlayers;

    @ElementCollection
    @JoinColumn (name="location")
    private List<String> location = new ArrayList<>();
    private Integer turn;

    public Salvo(){}

    public Salvo( GamePlayer gamePlayer, List<String> location, Integer turn) {
        this.gamePlayers = gamePlayer;
        this.location = location;
        this.turn = turn;
        gamePlayer.addSalvo(this);
    }
    //GETTERS
    public Long getId() {
        return id;
    }

    public GamePlayer getGamePlayers() {
        return gamePlayers;
    }

    public List<String> getLocation() {
        return location;
    }

    public Integer getTurn() {
        return turn;
    }
    //SETTERS

    public void setId(Long id) {
        this.id = id;
    }

    public void setGamePlayers(GamePlayer gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }
}
