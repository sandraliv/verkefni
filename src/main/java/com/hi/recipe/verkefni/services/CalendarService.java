package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Calendar;
import com.hi.recipe.verkefni.klasar.User;

import java.time.LocalDate;
import java.util.List;

public interface CalendarService {


    Calendar saveRecipeToCalendar(User user, Integer recipeId, Integer userRecipeId, LocalDate date);

    void removeRecipeFromCalendar(Integer userId, Integer recipeId, Integer userRecipeId, LocalDate date);

    List<Calendar> getUserSavedToCalendarRecipes(User user);

    List<Calendar> getUserSavedToCalendarRecipesForDateRange(User user, LocalDate startDate, LocalDate endDate);

    List<Object[]> countRecipesSavedToCalendar();

    Long countUsersUsingCalendar();

    Long countTotalSavedRecipes();
}
