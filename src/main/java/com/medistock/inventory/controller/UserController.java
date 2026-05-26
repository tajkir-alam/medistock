package com.medistock.inventory.controller;

import com.medistock.inventory.model.User;
import com.medistock.inventory.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable @NonNull Long id
    ) {

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @PathVariable String username
    ) {

        return userService.getByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(
            @RequestBody User user
    ) {

        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable @NonNull Long id,
            @RequestBody User user
    ) {

        return userService.getUserById(id)
                .map(existing -> {

                    user.setId(existing.getId());

                    return ResponseEntity.ok(
                            userService.saveUser(user)
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @NonNull Long id
    ) {

        if (userService.getUserById(id).isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}