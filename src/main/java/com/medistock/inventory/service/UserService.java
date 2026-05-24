package com.medistock.inventory.service;

import com.medistock.inventory.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getByUsername(String username);

    void deleteUser(Long id);
}