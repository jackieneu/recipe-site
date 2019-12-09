package com.teamtreehouse.recipesite.ingredient;

import java.util.List;

public interface IngredientService {
    List<Ingredient> findAll();
    Ingredient findById(Long id);
    void save(Ingredient ingredient);
    void save(List<Ingredient> ingredients);
    void delete(Ingredient ingredient);
}
