package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Users;

import java.util.List;

public interface UserService {
    List<Users> findAll();
    Users save(Users users);
    void delete(Users users);
    Users findById(int id);
}
