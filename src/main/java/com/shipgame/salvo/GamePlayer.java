package com.shipgame.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;


    @OneToMany (mappedBy = "gamePlayers", fetch = FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();
    @OneToMany (mappedBy =  "gamePlayers", fetch = FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();
    private Date date;

    public GamePlayer(){}

    public GamePlayer(Date date, Game game, Player player) {
        player.addGamePlayer(this); //call addGamePlayers from player
        game.addGamePlayer(this); //same from game
        this.date = date;
        this.game = game;
        this.player = player;
    }

    public void addShip(Ship ship){
        ships.add(ship);
        ship.setGamePlayers(this);
    }
    public void addSalvo (Salvo salvo){
        salvos.add(salvo);
        salvo.setGamePlayers(this);
    }
    //Getters
    public Date getDate() {
        return date;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public Long getId() {
        return id;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    //Setters
    public void setDate(Date date) {
        this.date = date;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }



    @Override
    public String toString() {
        return "GamePlayer{" +
                "id=" + id +
                ", player=" + player +
                ", game=" + game +
                ", date=" + date +
                '}';
    }


}
