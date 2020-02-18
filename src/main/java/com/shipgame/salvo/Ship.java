package com.shipgame.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Entity
public class Ship {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayers;
    @ElementCollection
    @JoinColumn (name="location")
    private List<String> location = new ArrayList<>();
    private String type;


    public Ship(){}

    public Ship(String type, List<String> location, GamePlayer gamePlayer) {
        this.type = type;
        this.location = location;
        this.gamePlayers = gamePlayer;
        gamePlayer.addShip(this);
    }



    //getters
    public String getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public List<String> getLocation() {
        return location;
    }

    public GamePlayer getGamePlayers() {
        return gamePlayers;
    }

    //setters
    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGamePlayers(GamePlayer gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
}
