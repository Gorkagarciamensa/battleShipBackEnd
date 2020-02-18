package com.shipgame.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {

		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
//	@Bean
//	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository){
//		return (args) -> {
//			//Game creation
//			Game game = new Game(new Date()); //Create a new game with the actual date
//			Game game1 = new Game (Date.from(game.getDate().toInstant().plusSeconds(3600))); //create a new game with date 1h later.
//			Game game2 = new Game (Date.from(game.getDate().toInstant().plusSeconds(7200)));
//			Game game3 = new Game(Date.from(game.getDate().toInstant().plusSeconds(10800)));
//			Game game4 = new Game(Date.from(game.getDate().toInstant().plusSeconds(14400)));
//			Game game5 = new Game(Date.from(game.getDate().toInstant().plusSeconds(18000)));
//			Game game6 = new Game(Date.from(game.getDate().toInstant().plusSeconds(21600)));
//			Game game7 = new Game(Date.from(game.getDate().toInstant().plusSeconds(25200)));
//
//			System.out.println(game + "---"+ game1 + "----" + game2);
//			gameRepository.save(game);
//			gameRepository.save(game1);
//			gameRepository.save(game2);
//			gameRepository.save(game3);
//			gameRepository.save(game4);
//			gameRepository.save(game5);
//			gameRepository.save(game6);
//			gameRepository.save(game7);
//
//			//player
//			Player player = new Player ("Jack Bauer", "j.bauer@ctu.gov", passwordEncoder().encode("24"));
//			Player player1 = new Player ("Chloe O'Brian", "c.obrian@ctu.gov", passwordEncoder().encode("42"));
//			Player player2 = new Player ("Kim Bauer","kim_bauer@gmail.com", passwordEncoder().encode("kb"));
//			Player player3 = new Player("Tony Almeida", "t.almeida@ctu.gov", passwordEncoder().encode("mole"));
//
//			playerRepository.save(player);
//			playerRepository.save(player1);
//			playerRepository.save(player2);
//			playerRepository.save(player3);
//
//
//
//			//game 1
//			GamePlayer gamePlayer = new GamePlayer(new Date(), game, player);
//			GamePlayer gamePlayer1 = new GamePlayer(new Date(), game, player1);
//			gamePlayerRepository.save(gamePlayer);
//			gamePlayerRepository.save(gamePlayer1);
//			//game 2
//			GamePlayer gamePlayer2 = new GamePlayer(new Date(), game1, player);
//			GamePlayer gamePlayer3 = new GamePlayer(new Date(), game1, player1);
//			gamePlayerRepository.save(gamePlayer2);
//			gamePlayerRepository.save(gamePlayer3);
//			//game 3
//			GamePlayer gamePlayer4 = new GamePlayer(new Date(), game2, player1);
//			GamePlayer gamePlayer5 = new GamePlayer(new Date(), game2, player3);
//			gamePlayerRepository.save(gamePlayer4);
//			gamePlayerRepository.save(gamePlayer5);
//			//game 4
//			GamePlayer gamePlayer6 = new GamePlayer(new Date(), game3, player1);
//			GamePlayer gamePlayer7 = new GamePlayer(new Date(), game3, player);
//			gamePlayerRepository.save(gamePlayer6);
//			gamePlayerRepository.save(gamePlayer7);
//			//game5
//			GamePlayer gamePlayer8 = new GamePlayer(new Date(), game4, player3);
//			GamePlayer gamePlayer9 = new GamePlayer(new Date(), game4, player);
//			gamePlayerRepository.save(gamePlayer8);
//			gamePlayerRepository.save(gamePlayer9);
//			//game6
//			GamePlayer gamePlayer10 = new GamePlayer(new Date(), game5, player2);
////			GamePlayer gamePlayer11= new GamePlayer(new Date(), game5, player2);
//			gamePlayerRepository.save(gamePlayer10);
////			gamePlayerRepository.save(gamePlayer11);
//
//			//game7
//			GamePlayer gamePlayer12 = new GamePlayer(new Date(), game6, player3);
////			GamePlayer gamePlayer13= new GamePlayer(new Date(), game6, player2);
//			gamePlayerRepository.save(gamePlayer12);
////			gamePlayerRepository.save(gamePlayer13);
//			//game8
//			GamePlayer gamePlayer14 = new GamePlayer(new Date(), game7, player2);
//			GamePlayer gamePlayer15= new GamePlayer(new Date(), game7, player3);
//			gamePlayerRepository.save(gamePlayer14);
//			gamePlayerRepository.save(gamePlayer15);
//
//			//ship
//			Ship ship = new Ship("Destroyer", Arrays.asList("H2","H3","H4"), gamePlayer);
//			Ship ship1 = new Ship("Submarine", Arrays.asList("E1","F1","G1"), gamePlayer);
//			Ship ship2 = new Ship ("Patrol Boat", Arrays.asList("B4","B5"), gamePlayer);
//			Ship ship3 = new Ship ("Destroyer", Arrays.asList("B5","C5","D5"), gamePlayer1);
//			Ship ship4 = new Ship ("Patrol Boat", Arrays.asList("F1","F2"), gamePlayer1);
//			Ship ship5 = new Ship ("Destroyer", Arrays.asList("B5","C5","D5"), gamePlayer2);//
//			Ship ship6 = new Ship ("Patrol Boat", Arrays.asList("C6","C7"), gamePlayer2);//
//			Ship ship7 = new Ship ("Submarine", Arrays.asList("A2","A3","A4"), gamePlayer3);//
//			Ship ship8 = new Ship ("Patrol Boat", Arrays.asList("G6","H6"), gamePlayer3);//
//			Ship ship9 = new Ship ("Destroyer", Arrays.asList("B5","C5","D5"), gamePlayer4);//--
//			Ship ship10= new Ship ("Patrol Boat", Arrays.asList("C6","C7"), gamePlayer4);//--
//			Ship ship11= new Ship ("Submarine", Arrays.asList("A2","A3","A4"), gamePlayer5);//
//			Ship ship12= new Ship ("Patrol Boat", Arrays.asList("G6","H6"), gamePlayer5);//
//			Ship ship13= new Ship ("Destroyer", Arrays.asList("B5","C5","D5"), gamePlayer6);//--
//			Ship ship14= new Ship ("Patrol Boat", Arrays.asList("C6","C7"), gamePlayer6);//--
//			Ship ship15= new Ship ("Submarine", Arrays.asList("A2","A3","A4"), gamePlayer7);//
//			Ship ship16= new Ship ("Patrol Boat", Arrays.asList("G6","H6"), gamePlayer7);//--
//			Ship ship17= new Ship ("Destroyer", Arrays.asList("B5","C5","D5"), gamePlayer8);//-
//			Ship ship18= new Ship ("Patrol Boat", Arrays.asList("C6","C7"), gamePlayer8);//-
//			Ship ship19= new Ship ("Submarine", Arrays.asList("A2","A3","A4"), gamePlayer9);//-
//			Ship ship20= new Ship ("Patrol Boat", Arrays.asList("G6","H6"), gamePlayer9);////
//			Ship ship21= new Ship ("Destroyer", Arrays.asList("B5","C5","D5"), gamePlayer10);//
//			Ship ship22= new Ship ("Patrol Boat", Arrays.asList("C6","C7"), gamePlayer10);//
//			Ship ship23= new Ship ("Destroyer", Arrays.asList("B5","C5","D5"), gamePlayer14);//
//			Ship ship24= new Ship ("Patrol Boat", Arrays.asList("C6","C7"), gamePlayer14);//
//			Ship ship25= new Ship ("Submarine", Arrays.asList("A2","A3","A4"), gamePlayer15);//
//			Ship ship26= new Ship ("Patrol Boat", Arrays.asList("G6","H6"), gamePlayer15);//
//
//			shipRepository.save(ship);
//			shipRepository.save(ship1);
//			shipRepository.save(ship2);
//			shipRepository.save(ship3);
//			shipRepository.save(ship4);
//			shipRepository.save(ship5);
//			shipRepository.save(ship6);
//			shipRepository.save(ship7);
//			shipRepository.save(ship8);
//			shipRepository.save(ship9);
//			shipRepository.save(ship10);
//			shipRepository.save(ship11);
//			shipRepository.save(ship12);
//			shipRepository.save(ship13);
//			shipRepository.save(ship14);
//			shipRepository.save(ship15);
//			shipRepository.save(ship16);
//			shipRepository.save(ship17);
//			shipRepository.save(ship18);
//			shipRepository.save(ship19);
//			shipRepository.save(ship20);
//			shipRepository.save(ship21);
//			shipRepository.save(ship22);
//			shipRepository.save(ship23);
//			shipRepository.save(ship24);
//			shipRepository.save(ship25);
//			shipRepository.save(ship26);
//
//			//salvo
//			//TURN 1  in GAME 1
//			Salvo salvo = new Salvo (gamePlayer,Arrays.asList("B5", "C5", "F1"),1);
//			Salvo salvo1 = new Salvo (gamePlayer1,Arrays.asList("B4", "B5", "B6"),1);
//			//TURN 2 in GAME 1
//			Salvo salvo2 = new Salvo (gamePlayer,Arrays.asList("F2", "D5"),2);
//			Salvo salvo3 = new Salvo (gamePlayer1,Arrays.asList("E1", "H3", "A2"),2);
//			//TURN 1 in GAME 2
//			Salvo salvo4 = new Salvo (gamePlayer2,Arrays.asList("A2", "A4", "G6"),1);
//			Salvo salvo5 = new Salvo (gamePlayer3,Arrays.asList("B5","D5", "C7"),1);
//			//TURN 2 in GAME 2
//			Salvo salvo6 = new Salvo (gamePlayer2,Arrays.asList("A3", "H6"),2);
//			Salvo salvo7 = new Salvo (gamePlayer3,Arrays.asList("C5", "C6"),2);
//			//TURN 1 in GAME 3
//			Salvo salvo8 = new Salvo (gamePlayer4,Arrays.asList("G6", "H6", "A4"),1);
//			Salvo salvo9 = new Salvo (gamePlayer5,Arrays.asList("H1", "H2", "H3"),1);
//			//TURN 2 in GAME 3
//			Salvo salvo10 = new Salvo (gamePlayer4,Arrays.asList("A2", "A3", "D8"),2);
//			Salvo salvo11= new Salvo (gamePlayer5,Arrays.asList("E1", "F2", "G3"),2);
//			//TURN 1 in GAME 4
//			Salvo salvo12 = new Salvo (gamePlayer6,Arrays.asList("A3", "A4", "F7"),1);
//			Salvo salvo13 = new Salvo (gamePlayer7,Arrays.asList("B5", "C6", "H1"),1);
//			//TURN 2 in GAME 4
//			Salvo salvo14 = new Salvo (gamePlayer6,Arrays.asList("A2", "G6", "H6"),2);
//			Salvo salvo15= new Salvo (gamePlayer7,Arrays.asList("C5", "C7", "D5"),2);
//			//TURN 1 in GAME 5
//			Salvo salvo16 = new Salvo (gamePlayer8,Arrays.asList("A1", "A2", "A3"),1);
//			Salvo salvo17 = new Salvo (gamePlayer9,Arrays.asList("B5", "B6", "C7"),1);
//			//TURN 2 in GAME 5
//			Salvo salvo18 = new Salvo (gamePlayer8,Arrays.asList("G6", "G7", "G8"),2);
//			Salvo salvo19= new Salvo (gamePlayer9,Arrays.asList("C6", "D6", "E6"),2);
//			//TURN 3 in GAME 5
//			Salvo salvo20= new Salvo (gamePlayer9,Arrays.asList("H1", "H8"),3);
//
//			salvoRepository.save(salvo);
//			salvoRepository.save(salvo1);
//			salvoRepository.save(salvo2);
//			salvoRepository.save(salvo3);
//			salvoRepository.save(salvo4);
//			salvoRepository.save(salvo5);
//			salvoRepository.save(salvo6);
//			salvoRepository.save(salvo7);
//			salvoRepository.save(salvo8);
//			salvoRepository.save(salvo9);
//			salvoRepository.save(salvo10);
//			salvoRepository.save(salvo11);
//			salvoRepository.save(salvo12);
//			salvoRepository.save(salvo13);
//			salvoRepository.save(salvo14);
//			salvoRepository.save(salvo15);
//			salvoRepository.save(salvo16);
//			salvoRepository.save(salvo17);
//			salvoRepository.save(salvo18);
//			salvoRepository.save(salvo19);
//			salvoRepository.save(salvo20);
//
//			//SCORE
//			//winner: game 1
//			Score score = new Score(1.00, player,game);
//			Score score1 = new Score (0.00,player1,game);
//			//tie: game 2
//			Score score2 = new Score(0.5, player, game1);
//			Score score3 = new Score (0.5, player1, game1);
//			//winner: game 3
//			Score score4 = new Score(1.00,player1, game2);
//			Score score5 = new Score (0.00,player3, game2);
//			//tie: game 4
//			Score score6 = new Score(0.5,player, game3);
//			Score score7 = new Score (0.5, player1, game3);
//
//
//			scoreRepository.save(score);
//			scoreRepository.save(score1);
//			scoreRepository.save(score2);
//			scoreRepository.save(score3);
//			scoreRepository.save(score4);
//			scoreRepository.save(score5);
//			scoreRepository.save(score6);
//			scoreRepository.save(score7);
//
//
//
//
//		};
//	}

}

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		http.authorizeRequests()

				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/ladderBoard").permitAll()
				.antMatchers("/api/register").permitAll()
				.antMatchers("/h2-console/**").permitAll()
				.antMatchers("/rest/**").hasAuthority("ADMIN")
				.antMatchers("/**").hasAuthority("USER")
				.anyRequest().fullyAuthenticated();

		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/app/login");

		http.logout().logoutUrl("/app/logout");
		http.headers().frameOptions().disable();
		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

		}
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		// The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD",
				"GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// will fail with 403 Invalid CORS request
		configuration.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
