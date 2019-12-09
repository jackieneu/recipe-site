package com.teamtreehouse.recipesite.role;

import com.teamtreehouse.recipesite.core.BaseEntity;

import javax.persistence.Entity;

@Entity
public class Role extends BaseEntity {
    private String name;

    public Role(){
        super();
        this.name = null;
    }

    public Role(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
