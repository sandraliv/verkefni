package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:key)")
    Optional<User> findByUsername(String key);

    Optional<User> findByEmail(String email);
}
