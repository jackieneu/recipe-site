package com.teamtreehouse.recipesite.ingredient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<Ingredient> findAll() {
        return (List<Ingredient>) ingredientRepository.findAll();
    }

    @Override
    public Ingredient findById(Long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    @Override
    public void save(List<Ingredient> ingredients) {
        ingredientRepository.saveAll(ingredients);
    }

    @Override
    public void delete(Ingredient ingredient) {
        ingredientRepository.delete(ingredient);
    }
}
