package com.teamtreehouse.recipesite.core;

import com.teamtreehouse.recipesite.ingredient.Ingredient;
import com.teamtreehouse.recipesite.recipe.Recipe;
import com.teamtreehouse.recipesite.recipe.RecipeRepository;
import com.teamtreehouse.recipesite.role.Role;
import com.teamtreehouse.recipesite.role.RoleRepository;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserRepository;
import com.teamtreehouse.recipesite.web.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseLoader implements ApplicationRunner {
    private final RecipeRepository recipes;

    private final UserRepository users;

    private final RoleRepository roles;

    @Autowired
    public DatabaseLoader(UserRepository users, RecipeRepository recipeRepository, RoleRepository roleRepository) {
        this.users = users;
        this.recipes = recipeRepository;
        this.roles = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Role adminRole = new Role("ROLE_ADMIN");
        Role userRole = new Role("ROLE_USER");
        roles.save(adminRole);
        roles.save(userRole);

        User admin = new User("jrlamb", "password", adminRole);
        User user10 = new User("user10", "password", userRole);
        User user20 = new User("user20", "password", userRole);
        users.save(admin);
        users.save(user10);
        users.save(user20);

        Authentication auth = new UsernamePasswordAuthenticationToken(admin, null,
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(auth);

        //Recipe 1
        Ingredient ingredient = new Ingredient("eggs", "fresh", 3);
        Ingredient ingredient1 = new Ingredient("milk", "cup", 1);
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient);
        ingredientList.add(ingredient1);
        List<String> instructions = new ArrayList<>();
        instructions.add("Crack eggs into bowl.");
        instructions.add("Whisk with fork");
        instructions.add("Cook on medium");
        Recipe recipe = new Recipe("Scrambled Eggs", "Eggs in a frothy scramble", Category.BREAKFAST,
                5, 10, "https://images.media-allrecipes.com/userphotos/600x600/642809.jpg",
                ingredientList, instructions, user10);
        recipes.save(recipe);

        //Recipe 2
        Ingredient ingredient2 = new Ingredient("macaroni", "oz", 6);
        Ingredient ingredient3 = new Ingredient("cheese", "oz", 6);
        Ingredient ingredient4 = new Ingredient("evaporated milk", "oz", 6);
        List<Ingredient> ingredientList2 = new ArrayList<>();
        ingredientList2.add(ingredient2);
        ingredientList2.add(ingredient3);
        ingredientList2.add(ingredient4);
        List<String> instructions2 = new ArrayList<>();
        instructions2.add("Boil macaroni with just enough water to cover noodles.");
        instructions2.add("When most of the water is evaporated, add milk.");
        instructions2.add("Melt the cheese into the macaroni and milk mixture.");
        instructions2.add("Serve warm and enjoy!");
        Recipe recipe2 = new Recipe("Macaroni & Cheese", "The best mac and cheese you will ever eat",
                Category.LUNCH, 5, 10,
                "https://images.media-allrecipes.com/userphotos/600x600/390253.jpg",
                ingredientList2, instructions2, user20);
        recipes.save(recipe2);

        //Recipe 3
        Ingredient ingredient5 = new Ingredient("eggs", "fresh", 2);
        Ingredient ingredient6 = new Ingredient("nutella", "jar", 1);
        Ingredient ingredient7 = new Ingredient("bread slices", "fresh", 2);
        List<Ingredient> ingredientList3 = new ArrayList<>();
        ingredientList3.add(ingredient5);
        ingredientList3.add(ingredient6);
        ingredientList3.add(ingredient7);
        List<String> instructions3 = new ArrayList<>();
        instructions3.add("Crack you eggs and beat them.");
        instructions3.add("Toast the bread.");
        instructions3.add("Cover bread with eggs. Fry 15 minutes.");
        instructions3.add("Serve covered in nutella topped with seasonal fruit!");
        Recipe recipe3 = new Recipe("Brioche French Toast with Nutella",
                "Yummy dessert toast covered in egg wash and topped with nutella and seasonal fruit",
                Category.BREAKFAST, 5, 15,
                "https://images.media-allrecipes.com/userphotos/600x600/1930006.jpg",
                ingredientList3, instructions3, user20);
        recipes.save(recipe3);

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
