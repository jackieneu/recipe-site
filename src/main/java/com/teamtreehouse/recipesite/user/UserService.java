package com.teamtreehouse.recipesite.user;

import com.teamtreehouse.recipesite.recipe.Recipe;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User save(User user);
    boolean toggleFavorite(User user, Recipe recipe);
    List<User> findAll();
}
