package com.teamtreehouse.recipesite.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.teamtreehouse.recipesite.recipe.Recipe.recipeComparator;

@Service
public class RecipeServiceImpl implements RecipeService{

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public List<Recipe> findAll() {
        List<Recipe> recipes = (List<Recipe>) recipeRepository.findAll();
        recipes.sort(recipeComparator);
        return recipes;
    }

    @Override
    public Recipe findById(Long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    @Override
    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }
}
