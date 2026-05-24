package com.medistock.inventory.service.impl;

import com.medistock.inventory.model.User;
import com.medistock.inventory.repository.UserRepository;
import com.medistock.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }
}