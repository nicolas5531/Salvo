package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();  //El hashset es un objeto de tipo lista

    //constructores     son los unicos que tienen el nombre igual que la clase, son las 2 maneras de crear las clases
    public Player() {       //SpringData usa "hibernate" que utiliza el constructor vacio por defecto
    }                        // sino no va a saber que (parametro) hay q utilizar para poder pasar el objeto.

    public Player(String user) {

        this.userName = user;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    @JsonIgnore         //evita la recursividad
    public List<Game> getGames(){
        return gamePlayers.stream()
                .map(gam -> gam.getGame())
                .collect(toList());
                //El método crea una nueva matriz con los resultados de llamar a una función para cada elemento de matriz.map()
        //El método llama a la función proporcionada una vez para cada elemento de una matriz, en orden.
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);
        dto.put("email", userName);
        return dto;
    }


    //Getter $ Setters
    public String getUserName() {
        return userName;
    }

    public long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;

    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Score getScoreGame(Game game) {
        return scores.stream().filter(score -> score.getGame().equals(game)).findFirst().orElse(null);
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

}



