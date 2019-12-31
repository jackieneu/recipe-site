package com.teamtreehouse.recipesite.service;

import com.teamtreehouse.recipesite.ingredient.Ingredient;
import com.teamtreehouse.recipesite.ingredient.IngredientRepository;
import com.teamtreehouse.recipesite.ingredient.IngredientService;
import com.teamtreehouse.recipesite.ingredient.IngredientServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService = new IngredientServiceImpl();

    @Test
    public void save_shouldReturnOneIngredient() throws Exception {
        Ingredient ingredient = new Ingredient("eggs", "fresh", 3);
        ingredient.setId(1L);

        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        Ingredient result = ingredientService.save(ingredient);

        assertEquals(1, result.getId().intValue());
        assertEquals("eggs", result.getItem());
        assertEquals("fresh", result.getCondition());
        assertEquals(3, result.getQuantity());
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    public void save_shouldReturnTwoIngredients() throws Exception {
        Ingredient ingredient = new Ingredient("eggs", "fresh", 3);
        ingredient.setId(1L);
        Ingredient ingredient1 = new Ingredient("milk", "cup", 1);
        ingredient1.setId(2L);
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient);
        ingredientList.add(ingredient1);

        when(ingredientRepository.saveAll(ingredientList)).thenReturn(ingredientList);
        assertEquals("saveAll should return two ingredients", 2, ingredientService.save(ingredientList).size());
        verify(ingredientRepository).saveAll(ingredientList);
    }
}
