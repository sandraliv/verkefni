package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Calendar;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.klasar.UserRecipe;
import com.hi.recipe.verkefni.repository.CalendarRepository;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.repository.UserRecipeRepository;
import com.hi.recipe.verkefni.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;
    private final UserRecipeRepository userRecipeRepository;


    @Autowired
    public CalendarServiceImpl(CalendarRepository calendarRepository , RecipeRepository recipeRepository,UserRecipeRepository userRecipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.calendarRepository = calendarRepository;
        this.userRecipeRepository = userRecipeRepository;
    }

    @Override
    public Calendar saveRecipeToCalendar(User user, Integer recipeId, Integer userRecipeId, LocalDate date) {
        Recipe recipe = null;
        UserRecipe userRecipe = null;

        if (recipeId != null) {
            recipe = recipeRepository.findById(recipeId).orElse(null);
        }
        if (userRecipeId != null) {
            userRecipe = userRecipeRepository.findById(userRecipeId).orElse(null);
        }

        // Ensure that either recipe or userRecipe is provided
        if (recipe == null && userRecipe == null) {
            throw new IllegalArgumentException("No valid recipe or user recipe provided.");
        }

        // Create the Calendar entry
        Calendar calendar = new Calendar(user, recipe, userRecipe, date);
        calendar = calendarRepository.save(calendar);

        // If a public recipe is saved, increment the savedToCalendarCount
        if (recipe != null) {
            recipe.setSavedToCalendarCount(recipe.getSavedToCalendarCount() + 1);
            recipeRepository.save(recipe); // Save the updated recipe
        }

        return calendar;
    }


    @Override
    public void removeRecipeFromCalendar(Integer userId, Integer recipeId, Integer userRecipeId, LocalDate date) {
        // Retrieve the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the calendar entry based on user, recipeId or userRecipeId, and date
        Calendar calendarEntry = calendarRepository.findByUserAndRecipeIdAndUserRecipeIdAndSavedCalendarDate(
                user, recipeId, userRecipeId, date);

        if (calendarEntry != null) {
            // If the recipe is public, decrement the savedToCalendarCount
            if (calendarEntry.getRecipe() != null) {
                Recipe recipe = calendarEntry.getRecipe();
                recipe.setSavedToCalendarCount(recipe.getSavedToCalendarCount() - 1);
                recipeRepository.save(recipe); // Save the updated recipe
            }

            // Remove the entry from the calendar
            calendarRepository.delete(calendarEntry);
        } else {
            throw new IllegalArgumentException("No calendar entry found for the provided recipe, user, and date.");
        }
    }


    @Override
    public List<Calendar> getUserSavedToCalendarRecipes(User user) {
        return calendarRepository.findByUser(user);
    }

    @Override
    public List<Calendar> getUserSavedToCalendarRecipesForDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return calendarRepository.findByUserAndSavedCalendarDateBetween(user, startDate, endDate);
    }

    @Override
    public List<Object[]> countRecipesSavedToCalendar() {
        return calendarRepository.countRecipesSavedToCalendar();
    }

    @Override
    public Long countUsersUsingCalendar() {
        return calendarRepository.countUsersUsingCalendar();
    }
    @Override
    public Long countTotalSavedRecipes() {
        return calendarRepository.countTotalSavedRecipes();
    }


}
