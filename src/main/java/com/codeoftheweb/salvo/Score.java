package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private Double points;
    private LocalDateTime finshDate;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    //Constructor
    public Score() {           //constructor por defecto ya q sino hibernate da error sin el...
    }

    public Score(LocalDateTime finshDate, Player player, Game game, Double points) {
        this.points = points;
        this.finshDate = finshDate;
        this.player = player;
        this.game = game;
    }

    public Map<String, Object> scoreDto() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("score", points);
        dto.put("finishDate", finshDate);
        return dto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public LocalDateTime getFinshDate() {
        return finshDate;
    }

    public void setFinshDate(LocalDateTime finshDate) {
        this.finshDate = finshDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}