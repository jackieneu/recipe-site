package com.teamtreehouse.recipesite.recipe;

import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Recipe.class)
public class RecipeEventHandler {
    private final UserRepository users;

    @Autowired
    public RecipeEventHandler(UserRepository users) {
        this.users = users;
    }

    @HandleBeforeCreate
    public void addRecipeBasedOnLoggedInUser(Recipe recipe){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = users.findByUsername(username);
        recipe.setCreatedBy(user);
    }
}
