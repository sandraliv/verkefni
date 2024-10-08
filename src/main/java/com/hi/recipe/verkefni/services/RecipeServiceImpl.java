package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository)
    {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> findAllPaginated() {
        int page = 0;  
        int size = 2; 

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findAllPaginated(pageable);
        System.out.println("Total elements: " + recipes.getTotalElements());  // Total number of records
        System.out.println("Total pages: " + recipes.getTotalPages());
        List<Recipe> recipeList = recipes.getContent();
        return recipeList;
    }

    @Override
    public Recipe save(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> findById(int id) {
        return recipeRepository.findById(id);
    }

    @Override
    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

    @Override
    public List<Recipe> findByTagsIn(Collection<RecipeTag> tags) {
        return recipeRepository.findByTagsIn(tags);
    }

    public Optional<Recipe> findRecipeById(int id){
        System.out.println("id = " + id);
        return recipeRepository.findById(id);
    }

    @Override
    public List<Recipe> findByTitleContainingIgnoreCase(String keyword) {
        System.out.println(keyword);
        System.out.println(recipeRepository.findByTitleContaining(keyword));
        return recipeRepository.findByTitleContaining(keyword);
    }

    @Override
    public List<Recipe> findByTitleAndTags(String title, Collection<RecipeTag> tags) {
        return recipeRepository.findByTitleContainingIgnoreCaseAndTagsIn(title, tags);
    }

    @Override
    public List<Recipe> findByDate(){

        int page = 0;  
        int size = 2; 

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findByDate(pageable);

        List<Recipe> recipeList = recipes.getContent();
        return recipeList;
    }
}
