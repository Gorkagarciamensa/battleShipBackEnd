package com.shipgame.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;


    private Date date;

    //mappedBy search "game" in the GamePlayer class.
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores = new HashSet<>();



    public Game (){}

    public Game (Date date) {
        this.date = date;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayers.add(gamePlayer);
    }

    public void addScore (Score score){
        scores.add(score);
    }

    public List<Player> getPlayers(){
        return gamePlayers.stream().map(gamePlayer -> gamePlayer.getPlayer()).collect(Collectors.toList());
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", date=" + date +
                ", gamePlayers=" + gamePlayers +
                ", scores=" + scores +
                '}';
    }
}
