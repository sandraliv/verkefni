package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Users;
import com.hi.recipe.verkefni.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Users save(Users users) {
        return userRepository.save(users);
    }

    @Override
    public void delete(Users users) {
        userRepository.delete(users);
    }

    @Override
    public Optional<Users> findById(int id) {
        return userRepository.findById(id);
    }
}
