package com.teamtreehouse.recipesite.service;


import com.teamtreehouse.recipesite.ingredient.Ingredient;
import com.teamtreehouse.recipesite.recipe.Recipe;
import com.teamtreehouse.recipesite.recipe.RecipeRepository;
import com.teamtreehouse.recipesite.recipe.RecipeService;
import com.teamtreehouse.recipesite.recipe.RecipeServiceImpl;
import com.teamtreehouse.recipesite.role.Role;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.web.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService = new RecipeServiceImpl();

    //searchAndFilter - null for searchTerm, null for category
    @Test
    public void searchAndFilter_ShouldReturnTwo() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setName("Scrambled Eggs");
        Recipe recipe2 = new Recipe();
        recipe2.setName("Mac and Cheese");
        List<Recipe> recipes = Arrays.asList(
                recipe1,
                recipe2
        );

        when(recipeRepository.findAll()).thenReturn(recipes);

        assertEquals("searchAndFilter should return two recipes when no search term or category is provided",
                2,recipeService.searchAndFilter(null, null).size());
        verify(recipeRepository).findAll();
    }

    //searchAndFilter - eggs for searchTerm, Breakfast for category
    @Test
    public void searchAndFilter_ShouldReturnOne() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setName("Scrambled Eggs");
        recipe1.setDescription("eggs");
        recipe1.setCategory(Category.BREAKFAST);
        Recipe recipe2 = new Recipe();
        recipe2.setName("Mac and Cheese");
        recipe2.setDescription("cheesy macaroni");
        recipe2.setCategory(Category.LUNCH);
        List<Recipe> recipes = Arrays.asList(
                recipe1,
                recipe2
        );

        String searchTerm = "eggs";
        when(recipeRepository.findByDescriptionIgnoreCaseContaining(searchTerm)).thenReturn(
                recipes.stream().filter(recipe -> {
                    return recipe.getName().equalsIgnoreCase("Scrambled Eggs");
                }).collect(Collectors.toList()));

        assertEquals("searchAndFilter should return one recipes when a search term and category are provided",
                1,recipeService.searchAndFilter("BREAKFAST", searchTerm).size());
        verify(recipeRepository).findByDescriptionIgnoreCaseContaining(searchTerm);
    }

    //searchAndFilter - null for searchTerm, Breakfast for category
    @Test
    public void searchAndFilter_WhenCategoryButSearchTermNullShouldReturnOne() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setName("Scrambled Eggs");
        recipe1.setDescription("eggs");
        recipe1.setCategory(Category.BREAKFAST);
        Recipe recipe2 = new Recipe();
        recipe2.setName("Mac and Cheese");
        recipe2.setDescription("cheesy macaroni");
        recipe2.setCategory(Category.LUNCH);
        List<Recipe> recipes = Arrays.asList(
                recipe1,
                recipe2
        );

        when(recipeRepository.findAll()).thenReturn(
                recipes.stream().filter(recipe -> {
                    return recipe.getCategory().getName().equalsIgnoreCase("Breakfast");
                }).collect(Collectors.toList()));

        assertEquals("searchAndFilter should return one recipes when a search term and category are provided",
                1,recipeService.searchAndFilter("BREAKFAST", null).size());
        verify(recipeRepository).findAll();
    }

    @Test
    public void findById_ShouldReturnOne() throws Exception {
        when(recipeRepository.findById(1L)).thenReturn(java.util.Optional.of(new Recipe()));
        assertThat(recipeService.findById(1L), instanceOf(Recipe.class));
        verify(recipeRepository).findById(1L);
    }

    @Test
    public void save_ShouldVerifyRecipe() throws Exception {
        Recipe recipe = buildRecipe();

        when(recipeRepository.save(recipe)).thenReturn(recipe);
        Recipe result = recipeService.save(recipe);

        assertEquals(1, result.getId().intValue());
        assertEquals("Scrambled Eggs", result.getName());
        assertEquals("Eggs in a frothy scramble", result.getDescription());
        assertEquals("Breakfast", result.getCategory().getName());
        assertEquals(5, result.getPrepTime());
        assertEquals(10, result.getCookTime());
        assertEquals("https://images.media-allrecipes.com/userphotos/600x600/642809.jpg", result.getImage());
        assertEquals(2, result.getIngredients().size());
        assertEquals(3, result.getInstructions().size());
        verify(recipeRepository).save(recipe);
    }

    private Recipe buildRecipe() {
        User userRole = new User("userRole", "password", new Role("ROLE_USER"));
        Ingredient ingredient = new Ingredient("eggs", "fresh", 3);
        ingredient.setId(1L);
        Ingredient ingredient1 = new Ingredient("milk", "cup", 1);
        ingredient1.setId(2L);
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient);
        ingredientList.add(ingredient1);
        List<String> instructions = new ArrayList<>();
        instructions.add("Crack eggs into bowl.");
        instructions.add("Whisk with fork");
        instructions.add("Cook on medium");
        Recipe recipe = new Recipe("Scrambled Eggs", "Eggs in a frothy scramble", Category.BREAKFAST,
                5, 10, "https://images.media-allrecipes.com/userphotos/600x600/642809.jpg",
                ingredientList, instructions, userRole);
        recipe.setId(1L);
        userRole.getFavorites().add(recipe);
        return recipe;
    }
}
