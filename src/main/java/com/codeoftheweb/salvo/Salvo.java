package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity     //Le dice a JPA que esta clase va a ser una tabla en la base de datos
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo() {

    }

    public Salvo(int turn, List<String> locations) {
        this.turn = turn;
        this.locations = locations;
    }

    private List<String> getHits() {        //Obtengo las naves del oponent que golpea el viewer

        Optional<GamePlayer> opponent = this.gamePlayer.getOpponent();

        if(opponent.isPresent()){

            List<String> myShots = this.locations;
            List<String> opponentLocations = new ArrayList<>();
            Set<Ship> opponentShips = opponent.get().getShips();

            opponentShips.forEach(ship -> opponentLocations.addAll(ship.getLocations()));

            return myShots.stream().filter(opponentLocations::contains) //es lo mismo que hacer .filter(shot -> opponentLocations.contains(shot)
                    .collect(Collectors.toList());
        }
        else{
            return new ArrayList<>();
        }
    }

    public List<Ship> getSunkenShips() {        //Obtengo las naves undidas del opponent
        Optional<GamePlayer> opponent = this.gamePlayer.getOpponent();

        if(opponent.isPresent()){
            List<String> allShots = new ArrayList<>();

            Set<Salvo> mySalvos = this.gamePlayer.getSalvoes().stream()
                    .filter(salvo -> salvo.getTurn() <= this.turn).collect(Collectors.toSet());

            Set<Ship> opponentShips = opponent.get().getShips();

            mySalvos.forEach(salvo -> allShots.addAll(salvo.getLocations()));

            return opponentShips.stream().filter(ship -> allShots.containsAll(ship.getLocations()))
                    .collect(Collectors.toList());
        }
        else{
            return new ArrayList<>();
        }
    }

    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("player", this.gamePlayer.getPlayer().getId());
        dto.put("turn", turn);
        dto.put("salvoLocations", locations);
        return dto;
    }

    public Map<String, Object> salvoHitDTO() {

        Map<String, Object> dto = new LinkedHashMap<>(); // Linked envia a Map de forma ordenada.

        dto.put("turn", this.turn);
        dto.put("hits", this.getHits());
        return dto;
    }

    public Map<String, Object> salvoSunkenDTO() {

        Map<String, Object> dto = new LinkedHashMap<>(); // Linked envia a Map de forma ordenada.

        dto.put("turn", this.turn);
        dto.put("sunken", this.getSunkenShips().stream().map(Ship::shipDTO));
        return dto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

}