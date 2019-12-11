package com.teamtreehouse.recipesite.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.teamtreehouse.recipesite.recipe.Recipe.recipeComparator;

@Service
public class RecipeServiceImpl implements RecipeService{

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public List<Recipe> searchAndFilter(String category, String searchTerm) {

        List<Recipe> recipes;

        if((category != null && !category.equals("ALL CATEGORIES")) && searchTerm != null){
            recipes = recipeRepository.findByNameStartsWith(searchTerm).stream().filter(recipe -> {
                return recipe.getCategory().getName().equalsIgnoreCase(category);
            }).collect(Collectors.toList());
        } else if((category != null && category.equals("ALL CATEGORIES")) && searchTerm != null){
            recipes = recipeRepository.findByNameStartsWith(searchTerm);
        } else if((category != null && !category.equals("ALL CATEGORIES")) && searchTerm == null){
            recipes = this.findAll().stream().filter(recipe -> {
                return recipe.getCategory().getName().equalsIgnoreCase(category);
            }).collect(Collectors.toList());
        } else{
            recipes = this.findAll();
        }

        recipes.sort(recipeComparator);
        return recipes;
    }

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
