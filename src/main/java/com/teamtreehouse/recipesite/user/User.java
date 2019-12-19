package com.teamtreehouse.recipesite.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamtreehouse.recipesite.core.BaseEntity;
import com.teamtreehouse.recipesite.recipe.Recipe;
import com.teamtreehouse.recipesite.role.Role;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User extends BaseEntity {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Column(unique = true)
    @Size(min = 6, max = 20)
    private String username;

    @JsonIgnore
    @NotEmpty
    private String password;

    @Column
    @NotNull
    @JsonIgnore
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "role_id")
    @JsonIgnore
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JsonIgnore
    private Set<Recipe> favorites = new HashSet<>();

    public User() {
        super();
        this.username = null;
        this.password = null;
        this.enabled = true;
        this.role = null;
    }

    public User(String username, String password, Role role) {
        this();
        this.username = username;
        setPassword(password);
        this.role = role;
        this.enabled = true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static PasswordEncoder getPasswordEncoder() {
        return PASSWORD_ENCODER;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Recipe> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<Recipe> favorites) {
        this.favorites = favorites;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
