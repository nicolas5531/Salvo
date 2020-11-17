package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication					//Le decimos de cierta manera que cree el application context
public class SalvoApplication {			//Es la clase principal de nuestra app
	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) { //Es el punto de inicio de la app
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean    //Es un saco que toma spring o bolsa que contiene datos y acciones. Todos los objetos que estan en Spring son beans. Al hacer esto metemos a toda la data en el Application context
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {	//
		return (args) -> {
			// save a couple of customers
			//PLAYER
			Player player1 = new Player ("j.bauer@ctu.gov", passwordEncoder.encode("242424"));
			playerRepository.save(player1);

			Player player2 = new Player ("c.obrian@ctu.gov", passwordEncoder.encode("424242"));
			playerRepository.save(player2);

			Player player3 = new Player ("t.almeida@ctu.gov", passwordEncoder.encode("molemole"));
			playerRepository.save(player3);

			Player player4 = new Player ("kim_bauer@gmail.com", passwordEncoder.encode("kbkbkb"));
			playerRepository.save(player4);
			//GAME
			Game game1 = new Game(LocalDateTime.now());
			gameRepository.save(game1);

			Game game2 = new Game(LocalDateTime.now().plusSeconds(3600));
			gameRepository.save(game2);

			Game game3 = new Game(LocalDateTime.now().plusHours(2));
			gameRepository.save(game3);

			Game game4 = new Game(LocalDateTime.now().plusHours(3));
			gameRepository.save(game4);

			Game game5 = new Game(LocalDateTime.now().plusHours(4));
			gameRepository.save(game5);

			Game game6 = new Game(LocalDateTime.now().plusHours(5));
			gameRepository.save(game6);

			Set<Ship> ships1 =  new HashSet<>();
			ships1.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("H2", "H3", "H4") )));
			ships1.add(new Ship("Submarine", new ArrayList<>(Arrays.asList("E1", "F1", "G1") )));
			ships1.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("B4", "B5") )));

			Set<Ship> ships11 =  new HashSet<>();
			ships11.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5") )));
			ships11.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("F1", "F2") )));

			Set<Ship> ships2 =  new HashSet<>();
			ships2.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5") )));
			ships2.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("C6", "C7") )));

			Set<Ship> ships22 =  new HashSet<>();
			ships22.add(new Ship("Submarine", new ArrayList<>(Arrays.asList("A2", "A3", "A4") )));
			ships22.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("G6", "H6") )));
			ships22.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5") )));
			ships22.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("C6", "C7") )));

			Set<Ship> ships3 =  new HashSet<>();
			ships3.add(new Ship("Submarine", new ArrayList<>(Arrays.asList("A2", "A3", "A4") )));
			ships3.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("G6", "H6") )));

			Set<Ship> ships33 =  new HashSet<>();
			ships33.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5") )));
			ships33.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("C6", "C7") )));

			Set<Ship> ships4 =  new HashSet<>();
			ships4.add(new Ship("Submarine", new ArrayList<>(Arrays.asList("A2", "A3", "A4") )));
			ships4.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("G6", "H6") )));

			Set<Ship> ships44 =  new HashSet<>();
			ships44.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5") )));
			ships44.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("C6", "C7") )));

			Set<Ship> ships5 =  new HashSet<>();
			ships5.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5") )));
			ships5.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("C6", "C7") )));

			Set<Ship> ships55 =  new HashSet<>();
			ships55.add(new Ship("Destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5") )));
			ships55.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("C6", "C7") )));

			Set<Ship> ships6 =  new HashSet<>();
			ships6.add(new Ship("Submarine", new ArrayList<>(Arrays.asList("A2", "A3", "A4") )));
			ships6.add(new Ship("Patrol Boat ", new ArrayList<>(Arrays.asList("G6", "H6") )));

			Set<Salvo> salvoes1 =  new HashSet<>();
			salvoes1.add(new Salvo( 1, new ArrayList<>(Arrays.asList("D3", "B5"))));
			salvoes1.add(new Salvo( 2, new ArrayList<>(Arrays.asList("G2", "E7"))));
			salvoes1.add(new Salvo(3, new ArrayList<>(Arrays.asList("H6"))));

			Set<Salvo> salvoes2 =  new HashSet<>();
			salvoes2.add(new Salvo( 1, new ArrayList<>(Arrays.asList("D4", "B6"))));
			salvoes2.add(new Salvo( 2, new ArrayList<>(Arrays.asList("F2", "D7"))));
			salvoes2.add(new Salvo(3, new ArrayList<>(Arrays.asList("I6"))));

			Set<Salvo> salvoes3 =  new HashSet<>();
			salvoes3.add(new Salvo( 1, new ArrayList<>(Arrays.asList("A3", "C7"))));
			salvoes3.add(new Salvo( 2, new ArrayList<>(Arrays.asList("B9", "D10"))));
			salvoes3.add(new Salvo(3, new ArrayList<>(Arrays.asList("H6"))));

			Set<Salvo> salvoes4 =  new HashSet<>();
			salvoes4.add(new Salvo( 1, new ArrayList<>(Arrays.asList("D3", "B5"))));
			salvoes4.add(new Salvo( 2, new ArrayList<>(Arrays.asList("G2", "E7"))));
			salvoes4.add(new Salvo(3, new ArrayList<>(Arrays.asList("H6"))));

			Set<Salvo> salvoes5 =  new HashSet<>();
			salvoes5.add(new Salvo( 1, new ArrayList<>(Arrays.asList("G1", "C5"))));
			salvoes5.add(new Salvo( 2, new ArrayList<>(Arrays.asList("A2", "J7"))));
			salvoes5.add(new Salvo(3, new ArrayList<>(Arrays.asList("H6"))));

			Set<Salvo> salvoes6 =  new HashSet<>();
			salvoes6.add(new Salvo( 1, new ArrayList<>(Arrays.asList("I3", "J1"))));
			salvoes6.add(new Salvo( 2, new ArrayList<>(Arrays.asList("H2", "H7"))));
			salvoes6.add(new Salvo(3, new ArrayList<>(Arrays.asList("H6"))));

			Set<Salvo> salvoes7 =  new HashSet<>();
			salvoes7.add(new Salvo( 1, new ArrayList<>(Arrays.asList("D3", "B5"))));
			salvoes7.add(new Salvo( 2, new ArrayList<>(Arrays.asList("G2", "E7"))));
			salvoes7.add(new Salvo(3, new ArrayList<>(Arrays.asList("H6"))));

			Set<Salvo> salvoes8 =  new HashSet<>();
			salvoes8.add(new Salvo( 1, new ArrayList<>(Arrays.asList("J3", "J5"))));
			salvoes8.add(new Salvo( 2, new ArrayList<>(Arrays.asList("G1", "H7"))));
			salvoes8.add(new Salvo(3, new ArrayList<>(Arrays.asList("G8"))));

			Set<Salvo> salvoes9 =  new HashSet<>();
			salvoes9.add(new Salvo( 1, new ArrayList<>(Arrays.asList("D5", "B3"))));
			salvoes9.add(new Salvo( 2, new ArrayList<>(Arrays.asList("G2", "E7"))));
			salvoes9.add(new Salvo(3, new ArrayList<>(Arrays.asList("H4"))));

			Set<Salvo> salvoes10 =  new HashSet<>();
			salvoes10.add(new Salvo( 1, new ArrayList<>(Arrays.asList("D2", "B4"))));
			salvoes10.add(new Salvo( 2, new ArrayList<>(Arrays.asList("G1", "E6"))));
			salvoes10.add(new Salvo(3, new ArrayList<>(Arrays.asList("H3"))));

			Set<Salvo> salvoes11 =  new HashSet<>();
			salvoes11.add(new Salvo( 1, new ArrayList<>(Arrays.asList("D4", "B6"))));
			salvoes11.add(new Salvo( 2, new ArrayList<>(Arrays.asList("G3", "E8"))));
			salvoes11.add(new Salvo(3, new ArrayList<>(Arrays.asList("H1"))));

			Score score1 = new Score(LocalDateTime.now(),player1, game1, 1.0);
			Score score2 = new Score(LocalDateTime.now(),player2, game1, 0.0);
			Score score3 = new Score(LocalDateTime.now(),player1, game2, 0.5);
			Score score4 = new Score(LocalDateTime.now(),player2, game2, 0.5);
			Score score5 = new Score(LocalDateTime.now(),player2, game3, 0.5);
			Score score6 = new Score(LocalDateTime.now(),player3, game3, 0.5);
			Score score7 = new Score(LocalDateTime.now(),player1, game4, 0.0);
			Score score8 = new Score(LocalDateTime.now(),player2, game4, 1.0);
			Score score9 = new Score(LocalDateTime.now(),player3, game5, 1.0);
			Score score10 = new Score(LocalDateTime.now(),player1, game5, 0.0);

			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
			scoreRepository.save(score8);
			scoreRepository.save(score9);
			scoreRepository.save(score10);

			//GamePlayer
			GamePlayer gamePlayer1 = new GamePlayer(player1, game1, ships1, salvoes1);
			GamePlayer gamePlayer2 = new GamePlayer(player2, game1, ships11,salvoes2);
			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);

			GamePlayer gamePlayer3 = new GamePlayer(player1, game2, ships2,salvoes3);
			GamePlayer gamePlayer4 = new GamePlayer(player2, game2, ships22,salvoes4);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);

			GamePlayer gamePlayer5 = new GamePlayer(player2, game3, ships3,salvoes5);
			GamePlayer gamePlayer6 = new GamePlayer(player3, game3, ships33,salvoes6);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);

			GamePlayer gamePlayer7 = new GamePlayer(player1, game4, ships4,salvoes7);
			GamePlayer gamePlayer8 = new GamePlayer(player2, game4, ships44,salvoes8);
			gamePlayerRepository.save(gamePlayer7);
			gamePlayerRepository.save(gamePlayer8);

			GamePlayer gamePlayer9 = new GamePlayer(player3, game5, ships5,salvoes9);
			GamePlayer gamePlayer10 = new GamePlayer(player1, game5, ships55,salvoes10);
			gamePlayerRepository.save(gamePlayer9);
			gamePlayerRepository.save(gamePlayer10);

			GamePlayer gamePlayer11 = new GamePlayer(player4, game6, ships6,salvoes11);
			gamePlayerRepository.save(gamePlayer11);

		};
	}
}
