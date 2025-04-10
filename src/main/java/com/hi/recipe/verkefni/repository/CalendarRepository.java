package com.hi.recipe.verkefni.repository;


import com.hi.recipe.verkefni.klasar.Calendar;
import com.hi.recipe.verkefni.klasar.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    // Fetch saved recipes for a user between a date range
    List<Calendar> findByUserAndSavedCalendarDateBetween(User user, LocalDate startDate, LocalDate endDate);

    // Fetch all saved recipes by a user
    List<Calendar> findByUser(User user);

    // Count how many times a recipe has been saved in the calendar
    @Query("SELECT c.recipe.id, COUNT(c) FROM Calendar c GROUP BY c.recipe.id ORDER BY COUNT(c) DESC")
    List<Object[]> countRecipesSavedToCalendar();

    // Count distinct users who have saved recipes to the calendar
    @Query("SELECT COUNT(DISTINCT c.user) FROM Calendar c")
    Long countUsersUsingCalendar();

    @Query("SELECT COUNT(c) FROM Calendar c")
    Long countTotalSavedRecipes();

    @Query("SELECT c FROM Calendar c WHERE c.user = :user AND c.recipe.id = :recipeId AND c.userRecipe.id = :userRecipeId AND c.savedCalendarDate = :savedCalendarDate")
    Calendar findByUserAndRecipeIdAndUserRecipeIdAndSavedCalendarDate(User user, Integer recipeId, Integer userRecipeId, LocalDate savedCalendarDate);

    // For public recipes
    @Query("SELECT c FROM Calendar c WHERE c.user = :user AND c.recipe.id = :recipeId AND c.userRecipe IS NULL AND c.savedCalendarDate = :savedCalendarDate")
    Calendar findByUserAndRecipeId(User user, Integer recipeId, LocalDate savedCalendarDate);

    // For user-created recipes
    @Query("SELECT c FROM Calendar c WHERE c.user = :user AND c.userRecipe.id = :userRecipeId AND c.recipe IS NULL AND c.savedCalendarDate = :savedCalendarDate")
    Calendar findByUserAndUserRecipeId(User user, Integer userRecipeId, LocalDate savedCalendarDate);


}
