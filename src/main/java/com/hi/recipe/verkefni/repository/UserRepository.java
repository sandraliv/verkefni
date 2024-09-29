package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
}
