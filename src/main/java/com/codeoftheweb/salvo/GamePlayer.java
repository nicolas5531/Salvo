package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)    //operacion en cascade() le dice a hibernate que cuando se actualice game players vea si hay que actualizar ships
    private Set<Ship> ships = new HashSet<>();      //instanciar o crear el objeto de set (q es una interface)

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();

    //Constructor
    public GamePlayer() {           //constructor por defecto ya q sino hibernate da error sin el...
    }

    public GamePlayer(Player newPlayer, Game newGame) {
        this.player = newPlayer;
        this.game = newGame;
    }

    public GamePlayer(Player player, Game game, Set<Ship> ships, Set<Salvo> salvoes) {
        this.date = LocalDateTime.now();
        this.player = player;
        this.game = game;
        ships.forEach(ship -> {
            ship.setGamePlayer(this);
            this.ships.add(ship);
        });
        salvoes.forEach(salvo -> {
            salvo.setGamePlayer(this);
            this.salvoes.add(salvo);
        });
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gpId", id);
        dto.put("player", player.toDTO());
        dto.put("score",getScore() != null ? getScore().getPoints() : null);      //OPERADOR TERNARIO o datoQpuedeSerNull :? datoAmostrarCuandoEsNull
        return dto;
    }

    public Map<String, Object> gameViewDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.game.getId());
        dto.put("created", this.game.getDate());
        dto.put("gamePlayers", this.game.getGamePlayers().stream().map(GamePlayer :: toDTO));
        dto.put("ships", this.ships.stream().map(Ship::shipDTO));
        dto.put("salvoes", this.game.getGamePlayers().stream()
                .flatMap(gamePlayer -> gamePlayer.getSalvoes()
                        .stream().map(Salvo::salvoDTO)));       //hago doble map pq traigo 2 players y el
        return dto;                                                        // flatmap es para evitar el map superior y solo me
    };                                                            //  devuelva un resultado final...
//flatMap arreglo multidimensional?? Reordena el arreglo para ubicarlo en un solo nivel sino tendria todos los objetos en diferentes niveles
    //Entonces flatMap es una combinacion de flat q aplana el arreglo y map que lo mapea para convertirlo en un nuevo arreglo plano...
    //Getter & Setters

    public Set<Ship> getShips() {
        return ships;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Salvo> getSalvoes(Salvo salvo) {
        return salvoes;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public Score getScore(){
        return player.getScoreGame(game);
    }

}

