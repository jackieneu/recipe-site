package com.teamtreehouse.recipesite.web.controller;

import com.teamtreehouse.recipesite.ingredient.Ingredient;
import com.teamtreehouse.recipesite.ingredient.IngredientService;
import com.teamtreehouse.recipesite.recipe.Recipe;
import com.teamtreehouse.recipesite.recipe.RecipeService;
import com.teamtreehouse.recipesite.role.Role;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserService;
import com.teamtreehouse.recipesite.web.Category;
import com.teamtreehouse.recipesite.web.FlashMessage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class RecipeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private RecipeController controller;

    @Mock
    private RecipeService recipeService;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private UserService userService;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void index_shouldIncludeRecipesAndRenderIndexView() throws Exception{
        Recipe recipe = recipeBuilder();
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);

        when(recipeService.searchAndFilter(null, null)).thenReturn(recipes);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"))
                .andExpect(model().attribute("recipes", recipes));
        verify(recipeService).searchAndFilter(null,null);
    }

    @Test
    public void detail_shouldIncludeRecipeAndRenderDetailView() throws Exception{
        Recipe recipe = recipeBuilder();

        when(recipeService.findById(1L)).thenReturn(recipe);

        mockMvc.perform(get("/recipe/detail/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("detail"))
                .andExpect(model().attribute("recipe", recipe));
        verify(recipeService).findById(1L);
    }

    @Test
    public void create_shouldRenderCreateView() throws Exception{
        mockMvc.perform(get("/recipe/create"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("edit"))
                .andExpect(model().attribute("action", "/recipe/create"));
    }

    @Test
    public void create_shouldRedirectToNewRecipe() throws Exception{
        doAnswer(invocation -> {
            Recipe recipe = (Recipe)invocation.getArguments()[0];
            recipe.setId(1L);
            return null;
        }).when(recipeService).save(any(Recipe.class));

        mockMvc.perform(
                post("/recipe/create")
                        .param("name", "Spaghetti")
                        .param("description", "Noodles with a tomato sauce")
                        .param("category", "DINNER")
                        .param("prepTime", "5")
                        .param("cookTime", "5")
                        .param("ingredients[0].item", "noodles")
                        .param("ingredients[0].condition", "oz")
                        .param("ingredients[0].quantity", "12")
                        .param("instructions[0]", "boil noodles"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.SUCCESS))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/detail/1"));
        verify(recipeService).save(any(Recipe.class));
    }

    //Try creating recipe without ingredients or instructions
    @Test
    public void create_withoutIngredientsOrInstructionsShouldReturnError() throws Exception{
        mockMvc.perform(
                post("/recipe/create")
                        .param("name", "Spaghetti")
                        .param("description", "Noodles with a tomato sauce")
                        .param("category", "DINNER")
                        .param("prepTime", "5")
                        .param("cookTime", "5"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.FAILURE))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/create"));
    }

    //Try creating recipe with bad input
    @Test
    public void create_withInvalidInputShouldReturnError() throws Exception{
        mockMvc.perform(
                post("/recipe/create")
                        .param("name", "")
                        .param("description", "")
                        .param("prepTime", "0")
                        .param("cookTime", "0"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.FAILURE))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/create"));
    }

    @Test
    public void edit_shouldIncludeRecipeAndRenderEditView() throws Exception{
        Recipe recipe = recipeBuilder();

        when(recipeService.findById(1L)).thenReturn(recipe);

        mockMvc.perform(get("/recipe/edit/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("edit"))
                .andExpect(model().attribute("recipe", recipe))
                .andExpect(model().attribute("action", "/recipe/update/1"));
        verify(recipeService).findById(1L);
    }

    @Test
    public void edit_shouldRedirectToEditedRecipe() throws Exception {
        doAnswer(invocation -> {
            Recipe recipe = (Recipe)invocation.getArguments()[0];
            recipe.setId(1L);
            return null;
        }).when(recipeService).save(any(Recipe.class));

        mockMvc.perform(
                post("/recipe/update/1")
                        .param("name", "SpaghettiEdited")
                        .param("description", "Edited noodles with a tomato sauce")
                        .param("category", "DINNER")
                        .param("prepTime", "5")
                        .param("cookTime", "5")
                        .param("ingredients[0].item", "noodles")
                        .param("ingredients[0].condition", "oz")
                        .param("ingredients[0].quantity", "12")
                        .param("instructions[0]", "boil noodles edited"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.SUCCESS))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/detail/1"));
        verify(recipeService).save(any(Recipe.class));
    }

    @Test
    public void edit_withoutIngredientOrInstructionsShouldReturnError() throws Exception {
        mockMvc.perform(
                post("/recipe/update/1")
                        .param("name", "SpaghettiEdited")
                        .param("description", "Edited noodles with a tomato sauce")
                        .param("category", "DINNER")
                        .param("prepTime", "5")
                        .param("cookTime", "5"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.FAILURE))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/edit/1"));
    }

    @Test
    public void toggleFavorite_ShouldRedirectToReferer() throws Exception {
        Recipe recipe = recipeBuilder();

        when(recipeService.findById(1L)).thenReturn(recipe);

        mockMvc.perform(post("/recipe/favorite/1")
                .header("referer", "/"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.SUCCESS))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(recipeService).findById(1L);
    }

    @Test
    public void delete_shouldRedirectToIndex() throws Exception {
        Recipe recipe = recipeBuilder();

        when(recipeService.findById(1L)).thenReturn(recipe);

        mockMvc.perform(post("/recipe/delete/1"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.SUCCESS))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(recipeService).findById(1L);
    }

    private Recipe recipeBuilder() {
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
        return recipe;
    }
}
