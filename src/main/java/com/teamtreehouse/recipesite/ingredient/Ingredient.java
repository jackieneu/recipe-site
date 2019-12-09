package com.teamtreehouse.recipesite.ingredient;

import com.teamtreehouse.recipesite.core.BaseEntity;

import javax.persistence.Entity;



@Entity
public class Ingredient extends BaseEntity {

    private String item;
    private String condition;
    private int quantity;

    public Ingredient() { super(); }

    public Ingredient(String item, String condition, int quantity) {
        this.item = item;
        this.condition = condition;
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
