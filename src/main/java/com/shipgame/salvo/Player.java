package com.shipgame.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany (mappedBy ="player", fetch = FetchType.EAGER)
    Set<Score> scores = new HashSet<>();

    private String userName;
    private String email;
    private String password;
    public Player (){}
    public Player(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;

    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayers.add(gamePlayer);
    }

    public void addScore (Score score){
        scores.add(score);
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public List<Game> getGames(){
        return gamePlayers.stream().map(GamePlayer::getGame).collect(Collectors.toList());
    }
    public List<Double> getScores() {
        return scores.stream().map(Score::getScore).collect(Collectors.toList());
    }
    public Score getPlayerScore(Game game){
        return this.scores.stream().filter(score -> score.getGame().equals(game)).findFirst().orElse(null);
    }

//getters
    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


    public String getUserName() {
        return userName;
    }

    //setters
    public void setId(long id) {
        this.id = id;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", gamePlayers=" + gamePlayers +
                ", scores=" + scores +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


}
