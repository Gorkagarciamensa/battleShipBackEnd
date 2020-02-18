package com.shipgame.salvo;




import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private  ScoreRepository scoreRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SalvoRepository salvoRepository;


    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    public ResponseEntity <Map<String, Object>>register(@RequestBody Player player) {

        if (player.getUserName().isEmpty() || player.getEmail().isEmpty() || player.getPassword().isEmpty()) {
            return new ResponseEntity<>(makeMap("Error", "Empty data"), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(player.getUserName()) !=  null) {
            return new ResponseEntity<>(makeMap( "Error","Name already in use"), HttpStatus.FORBIDDEN);
        }
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        Player newPlayer  = playerRepository.save(player);
        return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
    }
    @RequestMapping(value = "/api/game/{id}/players", method = RequestMethod.POST)
    public ResponseEntity <Map<String, Object>> joinGame(@PathVariable ("id") Long id, Authentication authentication){

        if (authentication == null){
            return new ResponseEntity<>(makeMap("Error", "empty data"), HttpStatus.UNAUTHORIZED);
        }
        Player player = loggedPlayer(authentication);
        if (player == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.UNAUTHORIZED);
        }
        Game game = gameRepository.getOne(id);

        if (game == null){
            return new ResponseEntity<>(makeMap("Error", "no such game"), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() == 2){
            return new ResponseEntity<>(makeMap("Error", "game is full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer newGamePlayer = new GamePlayer(new Date(), game, player);
        gamePlayerRepository.save(newGamePlayer);
        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity <Map<String, Object>> postShips(@PathVariable ("gamePlayerId") Long id, Authentication authentication, @RequestBody Set<Ship> ships, Salvo salvos){
        if (authentication == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.UNAUTHORIZED);
        }
        Player player = loggedPlayer(authentication);
        if(player == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);

        if (gamePlayer == null){
            return new ResponseEntity<>(makeMap("Error", "you can't join this game, because it doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        for (Ship ship : ships) {
            if (ship.getLocation().size() == 0) {
                return new ResponseEntity<>(makeMap("Error", "place ships before sending"), HttpStatus.FORBIDDEN);
            }
        }

        if (gamePlayer.ships.size() == 5){
            return new ResponseEntity<>(makeMap("Error", "All ships placed"), HttpStatus.FORBIDDEN);
        }

        ships.forEach(ship -> {
                gamePlayer.addShip(ship);
                shipRepository.save(ship);
        });
        return new ResponseEntity<>(makeMap("placed", "placed ships"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity <Map<String, Object>> postSalvos(@PathVariable ("gamePlayerId") Long id, Authentication authentication, @RequestBody List<String> salvoLoc){
        Player player = loggedPlayer(authentication);
        if (authentication == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.UNAUTHORIZED);
        }

        if(player == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);

        if (gamePlayer == null){
            return new ResponseEntity<>(makeMap("Error", "you can't join this game, because it doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.ships.size() < 5){
            return new ResponseEntity<>(makeMap("Error", "can't shoot if you don't have any ships placed"), HttpStatus.FORBIDDEN);
        }
        if ( opponentPlayer(gamePlayer).ships.size() < 5){
            return new ResponseEntity<>(makeMap("Error", "the opponent has to place the ships before shooting"), HttpStatus.FORBIDDEN);
        }



        if (salvoLoc.size() < 5){
            return new ResponseEntity<>(makeMap("Error", "you have to fire 5 shots!"), HttpStatus.FORBIDDEN);
        }
        if ((opponentPlayer(gamePlayer).salvos.size() < gamePlayer.salvos.size())){
            return new ResponseEntity<>(makeMap("Error", "Wait for the opponent player to shoot!"), HttpStatus.FORBIDDEN);
        }
        if (((makeSunkDTO(gamePlayer.salvos, gamePlayer).get("sunk").size() == 5)
                && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()) ||
                ((makeSunkDTO(opponentPlayer(gamePlayer).salvos, opponentPlayer(gamePlayer)).get("sunk").size() == 5)
                && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()) ||
                ((makeSunkDTO(gamePlayer.salvos, gamePlayer).get("sunk").size() == 5)
                        && (makeSunkDTO(opponentPlayer(gamePlayer).salvos, opponentPlayer(gamePlayer)).get("sunk").size() == 5)
                        && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size())){
            return new ResponseEntity<>(makeMap("Error", "Game Over"), HttpStatus.FORBIDDEN);
        }

        Salvo salvo = new Salvo(gamePlayer, salvoLoc, gamePlayer.salvos.size()+1);
        salvoRepository.save(salvo);

        return new ResponseEntity<>(makeMap("placed", "salvos shot"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/games", method = RequestMethod.POST)
        public ResponseEntity <Map<String, Object>> createGame(Authentication authentication){

        if (authentication == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.UNAUTHORIZED);
        }
        Player player = loggedPlayer(authentication);
        if (player == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.FORBIDDEN);
        }


        Game newGame = new Game(new Date());
        gameRepository.save(newGame);

        GamePlayer newGamePlayer = new GamePlayer(new Date(), newGame, player);
        gamePlayerRepository.save(newGamePlayer);

        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/ladderBoard", method= RequestMethod.GET)
    public List <Map<String, Object>> getLadderBoard(){
        return playerRepository.findAll().stream().map(player -> getPlayerDetailDTO(player)).collect(Collectors.toList());
    }

    private Map<String, Object> getPlayerDetailDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("Player", makePlayerDTO(player));
        dto.put("Score", player.getScores());
        return dto;
    }
    @RequestMapping(value = "/api/games", method= RequestMethod.GET)

    public Map<String, Object>getGames(Authentication authentication) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("games", gameRepository.findAll().stream().map(game->makeGameDTO(game)).collect(Collectors.toList()));
        if (authentication == null ){
           dto.put("player", null);
        } else {
            dto.put("player", makePlayerDTO(loggedPlayer(authentication)));
        }
         return dto;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("date", game.getDate());
        dto.put("gameplayers", game.getGamePlayers().stream().sorted(Comparator.comparing(GamePlayer::getId)).map(gamePlayer -> makeGamePlayerDTO(gamePlayer)));
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));

        return dto;
    }
    private Map<String, Object> makePlayerDTO (Player player){
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", player.getId() );
        dto.put ("username", player.getUserName());
        dto.put("email", player.getEmail());
        return dto;
    }

    @RequestMapping(value = "/api/game_view/{Id}", method= RequestMethod.GET)
    public ResponseEntity <Map<String, Object>> getOneGame(@PathVariable ("Id") Long Id, Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        if (authentication == null){
            return new ResponseEntity<>(makeMap("Error", "you have to log in!"), HttpStatus.UNAUTHORIZED);
        }
        Player player = loggedPlayer(authentication);
        if(player == null){
            return new ResponseEntity<>(makeMap("Error", "player doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        GamePlayer gamePlayer = gamePlayerRepository.getOne(Id);
        if(gamePlayer.equals(null)){
            return new ResponseEntity<>(makeMap("Error", "gameplayer doens't exist"), HttpStatus.UNAUTHORIZED);
        }
        if(!player.equals(gamePlayer.getPlayer())){
            return new ResponseEntity<>(makeMap("Error", "don't try to cheat"), HttpStatus.CONFLICT);
        }



        dto.put("Game", makeGameDTO(gamePlayer.getGame()));
        dto.put("Ship", gamePlayer.getShips().stream().map(this::makeShipDto));
        dto.put("mySalvo",gamePlayer.getSalvos().stream().sorted(Comparator.comparing(Salvo::getTurn)).map(this::makeSalvoDTO));
        dto.put("State", makeLogicDTO(gamePlayer));
        if (opponentPlayer(gamePlayer) != null){
            dto.put("oppSalvo", opponentPlayer(gamePlayer).getSalvos().stream().sorted(Comparator.comparing(Salvo::getTurn)).map(this::makeSalvoDTO));
            dto.put("hits", gamePlayer.getSalvos().stream()
                    .sorted(Comparator.comparing(Salvo::getTurn)).map(this::makeTurnDTO));
            dto.put("sunkShips", makeSunkDTO(gamePlayer.salvos, gamePlayer));

        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private Map<String, Object> makeShipDto(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("Type", ship.getType());
        dto.put("Locations", ship.getLocation());
        return dto;
    }
    private Map <String, Object> makeSalvoDTO(Salvo salvo){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("Turn", salvo.getTurn());
        dto.put("Location", salvo.getLocation());

        return dto;
    }
    private Map<Integer, List<String>> makeTurnDTO (Salvo salvo){
        Map<Integer, List<String>> dto = new LinkedHashMap<>();

        List<String> shotsHit = new ArrayList<>();
        for (Ship ship : opponentPlayer(salvo.getGamePlayers()).getShips()) {
            for (String loc : ship.getLocation()) {
                if (salvo.getLocation().contains(loc)) {
                    shotsHit.add(loc);
                }
              }
            }
        dto.put(salvo.getTurn(), shotsHit);

        return dto;
    }

    private Map<String, List<String>> makeSunkDTO(Set<Salvo> salvos, GamePlayer gamePlayer){
        Map<String, List<String>> dto = new LinkedHashMap<>();
        List<String> sunkShip = new ArrayList<>();

        for ( Ship ship : opponentPlayer(gamePlayer).getShips()) {

            int shipSize = ship.getLocation().size();
            for (Salvo salvo : salvos.stream().sorted(Comparator.comparing(Salvo::getTurn)).collect(Collectors.toList())){
                 for (String loc : ship.getLocation()) {
                    if (salvo.getLocation().contains(loc)){
                        shipSize = shipSize -1;
                        if(shipSize == 0){
                            sunkShip.add(ship.getType());

                        }
                    }
                 }
             }
          }

        dto.put("sunk",  sunkShip);
        return dto;
    }

    private Map<String, Object> makeLogicDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();

        if (opponentPlayer(gamePlayer) == null){
            dto.put("Logic", "wait for opponent player to join");
        } else{
            dto.put("Logic", "place the ships");
            if (gamePlayer.ships.size() > opponentPlayer(gamePlayer).ships.size()){
                dto.put("Logic", "wait for opponent ships");
            } else {
                if(gamePlayer.ships.size() == 5){
                    dto.put("Logic", "you can now shoot");
                }

                if (gamePlayer.salvos.size() > opponentPlayer(gamePlayer).salvos.size()){
                    dto.put("Logic", "wait for opponent to shoot");
                }
                if ((makeSunkDTO(gamePlayer.salvos, gamePlayer).get("sunk").size() == 5 &&
                        makeSunkDTO(opponentPlayer(gamePlayer).salvos, opponentPlayer(gamePlayer)).get("sunk").size() != 5
                        && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size())){

                    dto.put("Logic", "VICTORY");

                    if (gamePlayer.getPlayer().getPlayerScore(gamePlayer.getGame()) == null){
                        Score newScore = new Score(1.00, gamePlayer.getPlayer(), gamePlayer.getGame());
                        scoreRepository.save(newScore);
                    }


                }
                if ((makeSunkDTO(opponentPlayer(gamePlayer).salvos, opponentPlayer(gamePlayer)).get("sunk").size() == 5 &&
                        makeSunkDTO(gamePlayer.salvos, gamePlayer).get("sunk").size() != 5)
                     && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()){
                    dto.put("Logic", "DEFEAT");

                    if (gamePlayer.getPlayer().getPlayerScore(gamePlayer.getGame()) == null){
                        Score newScore = new Score(0.00, gamePlayer.getPlayer(), gamePlayer.getGame());
                        scoreRepository.save(newScore);
                    }


                }
                if ((makeSunkDTO(gamePlayer.salvos, gamePlayer).get("sunk").size() == 5)
                        && (makeSunkDTO(opponentPlayer(gamePlayer).salvos, opponentPlayer(gamePlayer)).get("sunk").size() == 5)
                        && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()){
                    dto.put("Logic", "DRAW");
                    if (gamePlayer.getPlayer().getPlayerScore(gamePlayer.getGame()) == null){
                        Score newScore = new Score(0.5, gamePlayer.getPlayer(), gamePlayer.getGame());
                        scoreRepository.save(newScore);
                        Score oppScore = new Score (0.5, opponentPlayer(gamePlayer).getPlayer(), opponentPlayer(gamePlayer).getGame());
                        scoreRepository.save(oppScore);
                    }

                }
            }


        }
        return dto;
    }




    //----------------------------GENERAL METHODS----------------------------------------
    //METHOD TO GET THE OPPONENT PLAYER
   private GamePlayer opponentPlayer (GamePlayer gamePlayer){
        return gamePlayer.getGame().getGamePlayers()
                .stream()
                .filter(gamePlayer1 -> !gamePlayer1.getId().equals(gamePlayer.getId()))
                .findFirst()
                .orElse(null);
    //We get into the game in gamePlayer. Because we need to get all the gameplayers in the game, we have to "getGamePlayers".
    //From that, we filter so we can say that if the Id of the gameplayer1 is not equal to the Id of the gameplayer, we return
    //The first id it finds, or else it'll return null.
   }




   private Player loggedPlayer (Authentication authentication){
        return playerRepository.findByUserName(authentication.getName());
   }
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
