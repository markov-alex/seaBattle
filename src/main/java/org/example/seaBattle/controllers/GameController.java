package org.example.seaBattle.controllers;

import org.example.seaBattle.domain.User;
import org.example.seaBattle.repository.UserRepository;
import org.example.seaBattle.seaBattleLogic.Coordinate;
import org.example.seaBattle.seaBattleLogic.battleField.BattleField;
import org.example.seaBattle.seaBattleLogic.battleField.CellState;
import org.example.seaBattle.seaBattleLogic.battleField.ResultOfShot;
import org.example.seaBattle.seaBattleLogic.game.Game;
import org.example.seaBattle.seaBattleLogic.game.GameState;
import org.example.seaBattle.seaBattleLogic.game.ResponseGame;
import org.example.seaBattle.seaBattleLogic.game.ResponseRecovery;
import org.example.seaBattle.seaBattleLogic.ship.Ship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class GameController {
    @Autowired
    private UserRepository userRepository;

    private static List<String> waitingUsers;
    private static Map<String, Game> readyGames;
    private static Map<String, Game> activeGames;

    static {
        waitingUsers = new LinkedList<>();
        readyGames = new HashMap<>();
        activeGames = new HashMap<>();
    }

    @GetMapping("/playground/newgame")
    public synchronized String newGame(@AuthenticationPrincipal User user) {
        if (!waitingUsers.contains(user.getUsername())) {
            waitingUsers.add(user.getUsername());
        }
        if (waitingUsers.size() >= 2) {
            Game game = new Game(UUID.randomUUID().toString(),
                    userRepository.findByUsername(waitingUsers.get(0)),
                    userRepository.findByUsername(waitingUsers.get(1)));
            readyGames.put(waitingUsers.get(0), game);
            readyGames.put(waitingUsers.get(1), game);
        }
        return "waiting_page";
    }

    @GetMapping("/playground/findgame")
    public synchronized String findGame(@AuthenticationPrincipal User user,
                           Model model) {
        if (!readyGames.containsKey(user.getUsername())) {
            return "waiting_page";
        }

        waitingUsers.remove(user.getUsername());
        Game game = readyGames.get(user.getUsername());
        if (!activeGames.containsKey(game.getId())) {
            activeGames.put(game.getId(), game);
        }
        readyGames.remove(user.getUsername());

        return "redirect:/playground/game/" + game.getId();
    }

    @GetMapping("/playground/game/{gameId}")
    public synchronized String getGamePage(@PathVariable String gameId,
                              @AuthenticationPrincipal User user,
                              Model model) {
        Game game = activeGames.get(gameId);
        String username = user.getUsername();
        String firstUsername = game.getFirstUser().getUsername();
        String secondUsername = game.getSecondUser().getUsername();
        String otherUsername;
        if (firstUsername.equals(username)) {
            otherUsername = secondUsername;
        } else if (secondUsername.equals(username)) {
            otherUsername = firstUsername;
        } else {
            return "playground";
        }
        model.addAttribute("playerUsername", username);
        model.addAttribute("opponentUsername", otherUsername);
        return "game";
    }

    @GetMapping(value = "/playground/game/{gameId}/isActive",
            produces = "application/json")
    public synchronized @ResponseBody boolean isActiveGame(@PathVariable String gameId,
                                                           @AuthenticationPrincipal User user) {
        Game game = activeGames.get(gameId);
        String username = user.getUsername();
        String firstUsername = game.getFirstUser().getUsername();
        String secondUsername = game.getSecondUser().getUsername();

        if (!username.equals(firstUsername) && !username.equals(secondUsername)) {
            return false;
        }

        if (username.equals(firstUsername) && game.getFirstBattleField() == null ||
            username.equals(secondUsername) && game.getSecondBattleField() == null) {
            return false;
        }

        return true;
    }

    @GetMapping(value = "/playground/game/{gameId}/recovery",
            produces = "application/json")
    public synchronized @ResponseBody ResponseRecovery recoverGame(@PathVariable String gameId,
                                           @AuthenticationPrincipal User user) {
        Game game = activeGames.get(gameId);
        String username = user.getUsername();
        String firstUsername = game.getFirstUser().getUsername();
        String secondUsername = game.getSecondUser().getUsername();

        if (!firstUsername.equals(username) && !secondUsername.equals(username)) {
            return new ResponseRecovery("AUTH_ERROR");
        }

        BattleField playerBattlefield = username.equals(firstUsername) ?
                game.getFirstBattleField() : game.getSecondBattleField();
        BattleField opponentBattlefield = !username.equals(secondUsername) ?
                game.getSecondBattleField() : game.getFirstBattleField();
        String nextMethod = username.equals(firstUsername) ?
                game.getCurrentMethodFirstUser() : game.getCurrentMethodSecondUser();

        ResponseRecovery responseRecovery = new ResponseRecovery();
        responseRecovery.setShips(playerBattlefield.getShips());
        responseRecovery.setPlayerShots(opponentBattlefield.recoverPreviousStateOfShots());
        responseRecovery.setOpponentShots(playerBattlefield.recoverPreviousStateOfShots());
        responseRecovery.setNextMethod(nextMethod);

        return responseRecovery;
    }

    @PostMapping(value = "/playground/game/{gameId}/ready",
            consumes = "application/json",
            produces = "application/json")
    public @ResponseBody ResponseGame playerReady(@PathVariable String gameId,
                                                  @AuthenticationPrincipal User user,
                                                  @RequestBody Coordinate[][] coordinates) {
        Game game = activeGames.get(gameId);
        if (game.getFirstUser().getUsername().equals(user.getUsername())) {
            game.setFirstBattleField(new BattleField(coordinates));
            synchronized (game.getCountOfReceivedRequests()) {
                if (!game.isFirstUserReady()) {
                    game.setCountOfReceivedRequests(game.getCountOfReceivedRequests() + 1);
                    game.setFirstUserReady(true);
                }
            }
            while (game.getCountOfReceivedRequests() < 2) {

            }
            game.setCurrentMethodFirstUser("POST");
            return new ResponseGame("POST");
        } else if (game.getSecondUser().getUsername().equals(user.getUsername())) {
            game.setSecondBattleField(new BattleField(coordinates));
            synchronized (game.getCountOfReceivedRequests()) {
                if (!game.isSecondUserReady()) {
                    game.setCountOfReceivedRequests(game.getCountOfReceivedRequests() + 1);
                    game.setSecondUserReady(true);
                }
            }
            while (game.getCountOfReceivedRequests() < 2) {

            }
            game.setCurrentMethodSecondUser("GET");
            return new ResponseGame("GET");
        }
        return null;
    }

    @PostMapping(value = "/playground/game/{gameId}/shot",
            consumes = "application/json",
            produces = "application/json")
    public @ResponseBody ResponseGame shotAndGetResultOfShot(@PathVariable String gameId,
                                                             @AuthenticationPrincipal User user,
                                                             @RequestBody Coordinate coordinate) {
        Game game = activeGames.get(gameId);
        String username = user.getUsername();
        String firstUsername = game.getFirstUser().getUsername();
        String secondUsername = game.getSecondUser().getUsername();

        if (!firstUsername.equals(username) && !secondUsername.equals(username)) {
            return new ResponseGame("AUTH_ERROR");
        }

        while (game.getGameState() != GameState.permissionPost) {
            if (firstUsername.equals(username) && game.getResultFirstUser() != null) {
                return new ResponseGame(game.getResultFirstUser());
            } else if (secondUsername.equals(username) && game.getResultSecondUser() != null) {
                return new ResponseGame(game.getResultSecondUser());
            }
        }
        BattleField bf;
        if (firstUsername.equals(username)) {
            bf = game.getSecondBattleField();
        } else {
            bf = game.getFirstBattleField();
        }
        ResultOfShot[] resultOfShot = bf.getResultOfShot(coordinate);
        String nextMethod = "GET";
        for (ResultOfShot res: resultOfShot) {
            if (res.getCoordinate().equals(coordinate) && res.getCellState() == CellState.BEATED_SHIP) {
                nextMethod = "POST";
                break;
            }
        }
        if (bf.allShipsTakedDown()) {
            nextMethod = "WINNER";
        }
        if (username.equals(firstUsername)) {
            game.setCurrentMethodFirstUser(nextMethod);
        } else {
            game.setCurrentMethodSecondUser(nextMethod);
        }
        ResponseGame responseGame = new ResponseGame(nextMethod, resultOfShot);
        game.setResponseGameOfPostRequest(responseGame);
        game.setGameState(GameState.permissionGet);
        while (game.getGameState() != GameState.endGet) {
            if (firstUsername.equals(username) && game.getResultFirstUser() != null) {
                game.setResponseGameOfPostRequest(null);
                game.setGameState(GameState.permissionPost);
                return new ResponseGame(game.getResultFirstUser());
            } else if (secondUsername.equals(username) && game.getResultSecondUser() != null) {
                game.setResponseGameOfPostRequest(null);
                game.setGameState(GameState.permissionPost);
                return new ResponseGame(game.getResultSecondUser());
            }
        }
        game.setResponseGameOfPostRequest(null);
        game.setGameState(GameState.permissionPost);
        return responseGame;
    }

    @GetMapping(value = "/playground/game/{gameId}/shot",
            produces = "application/json")
    public @ResponseBody ResponseGame getResultOfShot(@PathVariable String gameId,
                                                      @AuthenticationPrincipal User user) {
        Game game = activeGames.get(gameId);
        String username = user.getUsername();
        String firstUsername = game.getFirstUser().getUsername();
        String secondUsername = game.getSecondUser().getUsername();

        if (!firstUsername.equals(username) && !secondUsername.equals(username)) {
            return new ResponseGame("AUTH_ERROR");
        }

        while (game.getGameState() != GameState.permissionGet) {
            if (firstUsername.equals(username) && game.getResultFirstUser() != null) {
                game.setGameState(GameState.endGet);
                return new ResponseGame(game.getResultFirstUser());
            } else if (secondUsername.equals(username) && game.getResultSecondUser() != null) {
                game.setGameState(GameState.endGet);
                return new ResponseGame(game.getResultSecondUser());
            }
        }
        ResponseGame responseGameOfPostRequest = game.getResponseGameOfPostRequest();
        String nextMethod;
        if (responseGameOfPostRequest.getNextMethod().equals("POST")) {
            nextMethod = "GET";
        } else if (responseGameOfPostRequest.getNextMethod().equals("GET")) {
            nextMethod = "POST";
        } else {
            nextMethod = "LOOSER";
        }
        if (username.equals(firstUsername)) {
            game.setCurrentMethodFirstUser(nextMethod);
        } else {
            game.setCurrentMethodSecondUser(nextMethod);
        }
        ResultOfShot[] resultOfShot = responseGameOfPostRequest.getResultOfShot();
        ResponseGame responseGame = new ResponseGame(nextMethod, resultOfShot);
        game.setGameState(GameState.endGet);
        return responseGame;
    }

    @GetMapping(value = "/playground/game/{gameId}/opponentShips")
    public @ResponseBody Ship[] getOpponentShips(@PathVariable String gameId,
                                                 @AuthenticationPrincipal User user) {
        Game game = activeGames.get(gameId);
        String username = user.getUsername();
        String firstUsername = game.getFirstUser().getUsername();
        String secondUsername = game.getSecondUser().getUsername();

        if (!username.equals(firstUsername) && !username.equals(secondUsername)) {
            return null;
        }

        if (username.equals(firstUsername)) {
            return game.getSecondBattleField().getShips();
        } else {
            return game.getFirstBattleField().getShips();
        }
    }

    @GetMapping(value = "/playground/game/{gameId}/surrender",
            produces = "application/json")
    public synchronized @ResponseBody ResponseGame surrender(@PathVariable String gameId,
                            @AuthenticationPrincipal User user) {
        Game game = activeGames.get(gameId);
        String username = user.getUsername();
        String firstUsername = game.getFirstUser().getUsername();
        String secondUsername = game.getSecondUser().getUsername();

        if (!firstUsername.equals(username) && !secondUsername.equals(username)) {
            return new ResponseGame("AUTH_ERROR");
        }

        if (firstUsername.equals(username)) {
            if (game.getResultFirstUser() == null && game.getResultSecondUser() == null) {
                game.setResultFirstUser("LOOSER");
                game.setResultSecondUser("WINNER");
            }
            return new ResponseGame(game.getResultFirstUser());
        } else {
            if (game.getResultFirstUser() == null && game.getResultSecondUser() == null) {
                game.setResultFirstUser("WINNER");
                game.setResultSecondUser("LOOSER");
            }
            return new ResponseGame(game.getResultSecondUser());
        }
    }

    @GetMapping(value = "/playground/game/{gameId}/exit", produces = "application/json")
    public @ResponseBody ResponseGame exitGame(@PathVariable String gameId,
                                               @AuthenticationPrincipal User user) {
        if (activeGames.containsKey(gameId)) {
            Game game = activeGames.get(gameId);
            String username = user.getUsername();
            String firstUsername = game.getFirstUser() == null ? null : game.getFirstUser().getUsername();
            String secondUsername = game.getSecondUser() == null ? null : game.getSecondUser().getUsername();

            if (!username.equals(firstUsername) && !username.equals(secondUsername)) {
                return new ResponseGame("AUTH_ERROR");
            }

            if (username.equals(firstUsername)) {
                game.setFirstUser(null);
            } else {
                game.setSecondUser(null);
            }

            if (game.getFirstUser() == null && game.getSecondUser() == null) {
                activeGames.remove(gameId);
            }
            return new ResponseGame("SUCCESS");
        }

        return new ResponseGame("AUTH_ERROR");
    }
}
