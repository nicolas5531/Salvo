package com.codeoftheweb.salvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/game_view/{gamePlayerId}")     //RUTA
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {      //PathVariable dice q Spring me va a inyectar el num que salga de la ruta /game_vew/{gamePlayerId} El dato long ID
        Player user = playerRepository.findByUserName(authentication.getName());            //buscamos el user logeado
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId.longValue());
        if(gamePlayer.isPresent() && gamePlayer.get().getPlayer().getId() != user.getId()) {                                               //Para acceder al valor del optional necesesito el .get
            return new ResponseEntity<>(makeMap("error","NO ACCES"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(makeMap("gp", gamePlayer.get().gameViewDTO()), HttpStatus.OK);
    }

    @RequestMapping(path = "/player", method = RequestMethod.POST)
    private ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.length() < 4) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
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
