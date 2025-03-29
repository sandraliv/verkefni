package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.klasar.UserRecipe;
import com.hi.recipe.verkefni.repository.UserRecipeRepository;
import com.hi.recipe.verkefni.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRecipeServiceImpl implements UserRecipeService {
    private final UserRecipeRepository userRecipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserRecipeServiceImpl(UserRecipeRepository userRecipeRepository, UserRepository userRepository) {
        this.userRecipeRepository = userRecipeRepository;
        this.userRepository = userRepository;
    }


    @Override
    public UserRecipe uploadUserRecipe(UserRecipe userRecipe, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRecipe.setUser(user);
        return userRecipeRepository.save(userRecipe);
    }

    @Override
    public List<UserRecipe> getUserRecipes(int userId, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<UserRecipe> userRecipesPage = userRecipeRepository.findByUserId(userId, pageable);
        return userRecipesPage.getContent();
    }

    @Override
    public UserRecipe getUserRecipeByIdAndUser(int recipeId, int userId) {
        return userRecipeRepository.findByIdAndUserId(recipeId, userId);
    }

    @Override
    public void deleteUserRecipe(int recipeId, int userId) {
        UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(recipeId, userId);
        if (userRecipe != null) {
            userRecipeRepository.delete(userRecipe);
        }
    }
}
