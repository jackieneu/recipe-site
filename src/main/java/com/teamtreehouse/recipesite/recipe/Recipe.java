package com.teamtreehouse.recipesite.recipe;

import com.teamtreehouse.recipesite.core.BaseEntity;
import com.teamtreehouse.recipesite.ingredient.Ingredient;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.web.Category;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class Recipe extends BaseEntity {

    @NotNull
    @Size(min = 2, max = 140)
    private String name;

    @Size(max = 240)
    private String description;

    @NotNull(message = "category must be selected")
    private Category category;

    @NotNull
    @Min(value = 1, message = "Prep time must be greater than 0")
    private int prepTime;

    @NotNull
    @Min(value = 1, message = "Cook time must be greater than 0")
    private int cookTime;

    private String image;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "RECIPE_INGREDIENT",
            joinColumns = {@JoinColumn(name = "RECIPE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "INGREDIENT_ID")})
    private List<Ingredient> ingredients;

    @Column
    @ElementCollection(targetClass=String.class)
    private List<String> instructions;

    @ManyToOne
    private User createdBy;

    public Recipe(){
        super();
        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();
    }

    public Recipe(String name, String description, Category category, int prepTime, int cookTime, String image, List<Ingredient> ingredients, List<String> instructions, User createdBy) {
        this();
        this.name = name;
        this.description = description;
        this.category = category;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.image = image;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Ingredient ingredient){
        ingredients.add(ingredient);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public void addInstruction(String instruction){
        instructions.add(instruction);
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isFavored(User user) {
        return user.getFavorites().contains(this);
    }

    public static Comparator<Recipe> recipeComparator =
            (r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName());
}
