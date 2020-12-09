package com.codeoftheweb.salvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toSet;

                            // SalvoController es la comunicacion entre mi base de datos o back end y el front end
@RestController             //Esto le dice a Java que La clase SalvoController representa un controlador...El método controller debe devolver List<Object>   -----(linea 19)----
@RequestMapping("/api")     //METODO---despues de esto la direccion queda api/games @es una relacion de clase java.Estamos asosiando
public class SalvoController {

    @Autowired                               ////Instrucciones para obtener un conjunto de todos los juegos, le digo inyectame de manera automatica un gameRepository, crearlo o encontrarlo si ya esta creado.
    private GameRepository gameRepository;      //Es la clase que va a guardar la instancia de GameRepository

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping("/games")    //Por defecto los mapping son de tipo get como si fuera (path =/games, method= get)/Asociamos la peticion /games a la ejecucion de getGames //método al que se llama cuando la dirección URL es /api/games.
    public Map<String, Object> getAll(Authentication authentication) {         // Hace que obtenga todos los juegos y devuelva una lista de los ID.
        Player user = null;     //
        if(!isGuest(authentication)){
          user = playerRepository.findByUserName(authentication.getName()) ;              //pregunto esto pq auth puede ser null o ghest y explota ahi pq no puedo preguntar getName de eso...
        }
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", user !=null ?  user.toDTO() : "Guest");
        dto.put("games", gameRepository.findAll().stream().sorted(Comparator.comparing(game -> game.getId()))
                .map(Game::toDTO)
                .collect(toSet()) );
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> newGame (Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }

        Player newPlayer = playerRepository.findByUserName(authentication.getName());
        Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
        GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(newPlayer, newGame));

        return new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()), HttpStatus.CREATED);
    }
    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame (@PathVariable Long gameId ,Authentication authentication) {        //ASosiacion de un nuevo gp con un juego existente

        if (isGuest(authentication)) {                                                              //pregunto si hay usuario log o es invitado
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.UNAUTHORIZED);      //error si usuario actual es nulo
        }
        Optional<Game> game = gameRepository.findById(gameId.longValue());                     //id del juego al que me quiero unir
        if(game.isEmpty()){
            return new ResponseEntity<>(makeMap("error", "\"No such game\""), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerRepository.findByUserName(authentication.getName());           //authentic guarda quien es el usuario log
        GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(newPlayer, game.get()));
        return new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()), HttpStatus.CREATED);
    }
    @RequestMapping("/game_view/{gamePlayerId}")     //RUTA
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {      //PathVariable dice q Spring me va a inyectar el num que salga de la ruta /game_vew/{gamePlayerId} El dato long ID
        Player onlineUser = playerRepository.findByUserName(authentication.getName());            //buscamos el user logeado
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId.longValue());              //El optional nunca puede ser nullo, siempre va a llegar una caja,

        if(gamePlayer.isPresent() && gamePlayer.get().getPlayer().getId() != onlineUser.getId()) {       //                               //Para acceder al valor del optional necesesito el .get/ Si no hay nada en el optional el .get genera error
            return new ResponseEntity<>(makeMap("error","NO ACCESS"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(makeMap("gp", gamePlayer.get().gameViewDTO()), HttpStatus.OK);
    }

    @RequestMapping(path = "/player", method = RequestMethod.POST)
    private ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {                //Requestparam decimos que va a obtener el parametro desde la uri que nosotros creamos en el front end por ejemplo.

        if (email.isEmpty() || password.length() < 4) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> newShips (Authentication authentication, @PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships) {  //Digo QUE voy a retornar en la funcion newShips
                                                                                                        //RequestBody spring va a intentar sacar del request body la lista de naves

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Player onlineUser = playerRepository.findByUserName(authentication.getName());            //buscamos el user logeado
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);                     //id del gPlayer al que quiero poner las naves

        if(gamePlayer.isEmpty()) {                                      // Si esta vacio entra el error
            return new ResponseEntity<>(makeMap("error","not game player"), HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayer.get().getPlayer().getId() != onlineUser.getId()){         //comparo si corresponde el usuario log con el jugador q corresp al gam play
            return new ResponseEntity<>(makeMap("error","NO ACCESS "), HttpStatus.FORBIDDEN);
        }

        if(gamePlayer.get().getShips().size() > 0 ) {                                      //Para acceder al valor del optional necesesito el .get/ Si no hay nada en el optional el .get genera error
            return new ResponseEntity<>(makeMap("error","ships already placed or too many ships"), HttpStatus.FORBIDDEN);
        }

        if(ships.size() != 5 ) {                                      //Para acceder al valor del optional necesesito el .get/ Si no hay nada en el optional el .get genera error
            return new ResponseEntity<>(makeMap("error","You should add 5 ships, no more, no less."), HttpStatus.FORBIDDEN);
        }
        ships.forEach((ship)-> {
            gamePlayer.get().addShip(ship);
        });

        gamePlayerRepository.save(gamePlayer.get());
        return new ResponseEntity<>(makeMap("success", "ships added"), HttpStatus.CREATED);
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
