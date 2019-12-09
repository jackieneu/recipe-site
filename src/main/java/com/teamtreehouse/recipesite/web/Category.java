package com.teamtreehouse.recipesite.web;

public enum Category {
    BREAKFAST ("Breakfast"),
    LUNCH ("Lunch"),
    DINNER ("Dinner"),
    DESSERT ("Dessert");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSelected(String category){
        if(category != null){
            return category.equals(name.replace(" ","").toUpperCase());
        }
        return false;
    }
}
