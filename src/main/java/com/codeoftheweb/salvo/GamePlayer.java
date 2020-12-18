package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


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
    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public Optional<GamePlayer> getOpponent(){
        return this.game.getGamePlayers()
                //Stream seria como una analogia que representa un espacio de trabajo
                .stream()
                .filter(gp -> gp.getId() != this.id)
                .findFirst();

    }

    public Map<String, Object> gamePlayerDTO() {
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
        dto.put("gamePlayers", this.game.getGamePlayers().stream()
                .map(GamePlayer :: gamePlayerDTO));
        dto.put("ships", this.ships.stream()
                .map(Ship::shipDTO));
        dto.put("salvoes", this.game.getGamePlayers().stream()
                .flatMap(gamePlayer -> gamePlayer.getSalvoes()
                .stream().map(Salvo::salvoDTO)));                           //hago doble map pq traigo 2 players y el
        // damage done to the enemy
        dto.put("hits", this.salvoes.stream().map(Salvo::salvoHitDTO));
        dto.put("sunken", this.getSalvoes().stream().map(Salvo::salvoSunkenDTO));

        // damage done by the enemy, to the gamePlayer
        Optional<GamePlayer> opponent = this.getOpponent();

        if(opponent.isPresent()) {
            dto.put("enemyHits", opponent.get().getSalvoes().stream().map(Salvo::salvoHitDTO));
            dto.put("enemySunken", opponent.get().getSalvoes().stream().map(Salvo::salvoSunkenDTO));
        } else {
            // array vacío es mejor para trabajar con forEach en el front end
            // sin necesitar el "if == null"
            dto.put("enemyHits", new ArrayList<>());
            dto.put("enemySunken", new ArrayList<>());
        }
        dto.put("status", this.getGameStatus());
        return dto;                                                        // flatmap es para evitar el map superior y solo me
    };                                                            //  devuelva un resultado final...
        //flatMap arreglo multidimensional?? Reordena el arreglo para ubicarlo en un solo nivel sino tendria todos los objetos en diferentes niveles
        //Entonces flatMap es una combinacion de flat q aplana el arreglo y map que lo mapea para convertirlo en un nuevo arreglo plano...

    public String getGameStatus(){
        Optional<GamePlayer> opponent = this.getOpponent();
        Optional<Salvo> lastSalvo = this.salvoes.stream().filter(salvo -> salvo.getTurn() == this.salvoes.size()).findFirst();

        if(this.ships.isEmpty()){
            return "PLACE_SHIPS";
        }else if(opponent.isEmpty()){
            return "WAIT_OPPONENT";
        }else{
            Optional<Salvo> lastSalvoOpponent = opponent.get().salvoes.stream().filter(salvo -> salvo.getTurn() == opponent.get().salvoes.size()).findFirst();
            int turno = 0;
            int sunks = 0;
            int turnoOpponet = 0;
            int sunksOpponent = 0;
            if(lastSalvo.isPresent()){
                turno = lastSalvo.get().getTurn();
                sunks = lastSalvo.get().getSunkenShips().size();
            };
            if(lastSalvoOpponent.isPresent()) {
                turnoOpponet = lastSalvoOpponent.get().getTurn();
                sunksOpponent = lastSalvoOpponent.get().getSunkenShips().size();
            };

            if(turno < turnoOpponet){
                return "FIRE";
            }else if(turno > turnoOpponet){
                return "WAIT";
            }else {
                if(sunks < 5 && sunksOpponent == 5){
                    return "LOST";
                }else if (sunks == 5  && sunksOpponent < 5){
                    return "WIN";
                }else if(sunks == 5 && sunksOpponent == 5){
                    return "TIE";
                }else{
                    if(this.id < opponent.get().getId()){
                        return "FIRE";
                    }else{
                        return "WAIT";
                    }
                }
            }
        }
    };

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

    //public Set<Salvo> getSalvoes(Salvo salvo) {
    //    return salvoes;
    //}

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

