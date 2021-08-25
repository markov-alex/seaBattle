package org.example.seaBattle.controllers;

import org.example.seaBattle.domain.User;
import org.example.seaBattle.repository.UserRepository;
import org.example.seaBattle.seaBattleLogic.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    public String newGame(@AuthenticationPrincipal User user) {
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
    public String findGame(@AuthenticationPrincipal User user,
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
    public String getGamePage(@PathVariable String gameId, Model model) {
        model.addAttribute("gameId", gameId);
        return "game";
    }


}
