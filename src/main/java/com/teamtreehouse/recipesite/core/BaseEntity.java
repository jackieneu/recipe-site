package com.teamtreehouse.recipesite.core;

import javax.persistence.*;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public BaseEntity() {id = null;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {this.id = id;}
}
