package com.teamtreehouse.recipesite.ingredient;

import java.util.List;

public interface IngredientService {
    List<Ingredient> findAll();
    Ingredient findById(Long id);
    Ingredient save(Ingredient ingredient);
    List<Ingredient> save(List<Ingredient> ingredients);
    void delete(Ingredient ingredient);
}
