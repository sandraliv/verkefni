package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Calendar;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.CalendarService;
import com.hi.recipe.verkefni.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


@RestController
@RequestMapping("/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    private final UserService userService;


    public CalendarController(CalendarService calendarService,UserService userService) {
        this.calendarService = calendarService;
        this.userService = userService;

    }

    @PostMapping("/saveToDate")
    public ResponseEntity<Calendar> saveRecipeToCalendar(
            @RequestParam("userId") int userId,
            @RequestParam(value = "recipeId", required = false) Integer recipeId,
            @RequestParam(value = "userRecipeId", required = false) Integer userRecipeId,
            @RequestParam("date") String date) {

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Ensure only one of recipeId or userRecipeId is provided
        if ((recipeId == null && userRecipeId == null) || (recipeId != null && userRecipeId != null)) {
            throw new IllegalArgumentException("You must provide either a recipeId or a userRecipeId, but not both.");
        }

        LocalDate localDate = LocalDate.parse(date);
        Calendar savedCalendar = calendarService.saveRecipeToCalendar(user, recipeId, userRecipeId, localDate);
        return ResponseEntity.ok(savedCalendar);
    }

    // Endpoint to get all recipes from user that are saved to calendar
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Calendar>> getUserSavedToCalendarRecipes(@PathVariable("userId") int userId) {

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<Calendar> savedToCalendarRecipes = calendarService.getUserSavedToCalendarRecipes(user);
        return ResponseEntity.ok(savedToCalendarRecipes);
    }


    // Endpoint to get saved recipes within a date range
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<Calendar>> getUserSavedRecipesForDateRange(
            @PathVariable("userId") int userId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        // Fetch the user using the userId
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<Calendar> savedRecipes = calendarService.getUserSavedToCalendarRecipesForDateRange(user, start, end);
        return ResponseEntity.ok(savedRecipes);
    }
    //how many recipes are saved to calendar
    @GetMapping("/countTotalSavedRecipes")
    public ResponseEntity<Long> getTotalSavedRecipes() {
        Long totalSavedRecipes = calendarService.countTotalSavedRecipes();
        return ResponseEntity.ok(totalSavedRecipes);
    }
     //   how many times a recipe has been saved in the calendar
    @GetMapping("/countRecipesSaved")
    public ResponseEntity<List<Object[]>> getRecipesSavedCount() {
        List<Object[]> savedRecipeCounts = calendarService.countRecipesSavedToCalendar();
        return ResponseEntity.ok(savedRecipeCounts);
    }
    //   how many users have/are used the calendar
    @GetMapping("/users/count")
    public ResponseEntity<Long> getUsersCountUsingCalendar() {
        Long userCount = calendarService.countUsersUsingCalendar();
        return ResponseEntity.ok(userCount);
    }

    @DeleteMapping("/removeRecipeFromCalendar")
    public ResponseEntity<String> removeRecipeFromCalendar(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "recipeId", required = false) Integer recipeId,
            @RequestParam(value = "userRecipeId", required = false) Integer userRecipeId,
            @RequestParam(value = "dateEntry") String date) {

        try {
            // Validate that userId is provided and valid
            if (userId == null || userId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid userId");
            }

            // Remove any surrounding quotes in the date string if present
            date = date.replaceAll("\"", "");

            // Validate the date format
            LocalDate localDate;
            try {
                localDate = LocalDate.parse(date); // Parsing the date string to LocalDate
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Please provide a date in the format YYYY-MM-DD.");
            }

            // Log the incoming request parameters (optional, for debugging)
            System.out.println("Received request to remove recipe from calendar: userId = " + userId + ", recipeId = " + recipeId + ", userRecipeId = " + userRecipeId + ", date = " + localDate);

            // Ensure either recipeId or userRecipeId is provided, but not both
            if (recipeId == null && userRecipeId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Either recipeId or userRecipeId must be provided.");
            }

            if (recipeId != null && userRecipeId != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot provide both recipeId and userRecipeId. Provide only one.");
            }

            // Call the service method to remove the recipe from the calendar
            calendarService.removeRecipeFromCalendar(userId, recipeId, userRecipeId, localDate);

            // Return success response
            return ResponseEntity.ok("Recipe removed from calendar.");
        } catch (IllegalArgumentException e) {
            // Handle cases where recipe or userRecipe doesn't exist or isn't found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DateTimeParseException e) {
            // Handle specific exception for invalid date format
            return ResponseEntity.badRequest().body("Invalid date format. Please provide a date in the format YYYY-MM-DD.");
        } catch (Exception e) {
            // Handle unexpected errors
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the recipe.");
        }
    }

}


