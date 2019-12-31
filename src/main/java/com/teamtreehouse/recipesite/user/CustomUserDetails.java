package com.teamtreehouse.recipesite.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamtreehouse.recipesite.core.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CustomUserDetails extends BaseEntity implements UserDetails {

    public CustomUserDetails(){}

    public CustomUserDetails(User user) {
        this.user = user;
    }

    private User user;

    Set<GrantedAuthority> authorities=null;

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        return authorities;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities)
    {
        this.authorities=authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public String getPassword() {
        return user.getPassword();
    }

    public String getUsername() {
        return user.getUsername();
    }
}
