package com.teamtreehouse.recipesite.ingredient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Ingredient save(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public List<Ingredient> save(List<Ingredient> ingredients) {
        List<Ingredient> result = new ArrayList<>();
        ingredientRepository.saveAll(ingredients).forEach(result::add);
        return result;
    }

    @Override
    public void delete(Ingredient ingredient) {
        ingredientRepository.delete(ingredient);
    }
}
