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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<User> students = Arrays.asList(
                new User("jacobproffer", "password", userRole),
                new User("mlnorman", "password", userRole),
                new User("k_freemansmith", "password", userRole),
                new User("seth_lk", "password", userRole),
                new User("mrstreetgrid", "password", userRole),
                new User("anthonymikhail", "password", userRole),
                new User("boog690", "password", userRole),
                new User("faelor", "password", userRole),
                new User("christophernowack", "password", userRole),
                new User("calebkleveter", "password", userRole),
                new User("richdonellan", "password", userRole),
                new User("albertqerimi", "password", userRole)
        );
        users.saveAll(students);
        users.save(admin);

        Ingredient ingredient = new Ingredient("eggs", "fresh", 3);
        Ingredient ingredient1 = new Ingredient("milk", "cup", 1);
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient);
        ingredientList.add(ingredient1);
        List<String> instructions = new ArrayList<>();
        instructions.add("Crack eggs into bowl.");
        instructions.add("Whisk with fork");
        instructions.add("Cook on medium");
        Recipe recipe = new Recipe("Scrambled Eggs", "Eggs in a frothy scramble", Category.BREAKFAST, 5, 10, "url", ingredientList, instructions, admin);
        recipes.save(recipe);
    }
}
