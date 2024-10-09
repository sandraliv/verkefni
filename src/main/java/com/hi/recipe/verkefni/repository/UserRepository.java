package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    @Query("SELECT  u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :key, '%'))")
    Optional<User> findByUsername(String key);
}
