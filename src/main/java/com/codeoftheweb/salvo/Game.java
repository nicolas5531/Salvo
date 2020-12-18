package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime date;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();  //El hashset es un objeto de tipo lista

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();  //El hashset es un objeto de tipo lista


    //Constructor
    public Game() {
        this.date = LocalDateTime.now();
    }

    public Game(LocalDateTime date) {
        this.date = date;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);           //le digo a gamePlayer que this va a ser esa entidad.
        gamePlayers.add(gamePlayer);
    }

    //El método List.stream() crea un objeto de secuencia que tiene métodos como filter(), sorted() y map()
    // ,que toman una secuencia y devuelven una nueva secuencia.
    // El método collect() al final convierte una secuencia en una colección

    public List<Player> getPlayers() {
        return gamePlayers.stream()
                .map(sub -> sub.getPlayer())
                .collect(toList());
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);                         //podria poner this o no ya que la propiedad esta dentro de la clase.
        dto.put("created", date);
        dto.put("gamePlayers", gamePlayers.stream().map(GamePlayer::gamePlayerDTO));
        return dto;
    }
    //Getter & Setter
    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Set<Score> getScore() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

// public LocalDateTime getDate() {
    //    LocalDateTime date = LocalDateTime.now();
    //    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY, hh:mm a");
   //     return println(dtf.format(localDate));
   // }


}

