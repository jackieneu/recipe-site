package com.teamtreehouse.recipesite.user;

import com.teamtreehouse.recipesite.recipe.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean toggleFavorite(User user, Recipe recipe) throws UsernameNotFoundException {
        if(user == null){
            throw new UsernameNotFoundException("User not found!");
        }
        if(user.getFavorites().contains(recipe)){
            user.getFavorites().remove(recipe);
            return false;
        }else{
            user.getFavorites().add(recipe);
            return true;
        }
    }

    @Override
    public List<User> findAll(){
        return (List<User>) userRepository.findAll();
    }
}
