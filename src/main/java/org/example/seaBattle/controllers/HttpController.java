package org.example.seaBattle.controllers;

import org.example.seaBattle.domain.Role;
import org.example.seaBattle.domain.User;
import org.example.seaBattle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HttpController {
    @Autowired
    private UserRepository userRepository;

//    private static List<User> waitingUsers;
//    private static Map<String, Game> gameSessions;
//
//    static {
//        waitingUsers = new LinkedList<>();
//        gameSessions = new HashMap<>();
//    }

    @GetMapping("/")
    public String getMainPage() {
        return "index";
    }

    @GetMapping("/playground")
    public String play() {
        return "playground";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam(name = "username") String username,
                          @RequestParam(name = "password") String password,
                          Model model) {
        User user = new User(username, password);

        User userFromDb = userRepository.findByUsername(username);

        if (userFromDb != null) {
            model.addAttribute("messageAdd", "Пользователь с таким логином уже существует");
            return "registration";
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/users")
    public String allUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "edit_user";
    }

    @PostMapping("/users/{user}/edit")
    public String userEdit(@PathVariable User user,
                           @RequestParam("username") String username,
                           @RequestParam Map<String, String> form) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key: form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepository.save(user);
        return "redirect:/users";
    }

    @PostMapping("/users/filter")
    public String filterByUsername(@RequestParam String username, Model model) {
        List<User> users;
        if (username == null || username.isEmpty()) {
            users = userRepository.findAll();
        } else {
            User userFromDb = userRepository.findByUsername(username);
            if (userFromDb == null) {
                users = userRepository.findAll();
                model.addAttribute("messageFindByUsername", "Такого пользователя не существует");
            } else {
                users = Collections.singletonList(userRepository.findByUsername(username));
            }
        }
        model.addAttribute("users", users);
        return "users";
    }

//    @GetMapping("/playground/newgame")
//    public String newGame(@AuthenticationPrincipal User user) {
////        waitingUsers.add(user);
//////        synchronized (this) {
//////            System.out.println("ya tut " + Thread.currentThread().getName());
//////
//////        }
////        while (true) {
////            if (waitingUsers.size() >= 2) {
////                synchronized (HttpController.class) {
////                    if (waitingUsers.size() >= 2) {
////                        Game game = new Game(waitingUsers.remove(0), waitingUsers.remove(1));
////                        gameSessions.put(UUID.randomUUID().toString(), game);
////                        return "game";
////                    }
////
////                }
////            }
////        }
//
//        waitingUsers
//    }
}
