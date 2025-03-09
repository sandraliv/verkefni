package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    User save(User user);

    void delete(User user);

    void deleteById(int id);

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
