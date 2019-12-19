package com.teamtreehouse.recipesite.web.controller;

import com.teamtreehouse.recipesite.recipe.Recipe;
import com.teamtreehouse.recipesite.role.RoleRepository;
import com.teamtreehouse.recipesite.role.RoleService;
import com.teamtreehouse.recipesite.user.CustomUserDetails;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserService;
import com.teamtreehouse.recipesite.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        if (!model.containsAttribute("user")) {
            User user = new User();
            model.addAttribute("user", user);
        }
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String addUser(@Valid User user, BindingResult result, HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/signup";
        }
        user.setEnabled(true);
        user.setRole(roleService.findByName("ROLE_USER"));
        try {
            userService.save(user);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes
                    .addFlashAttribute("flash", new FlashMessage("Username already exists", FlashMessage.Status.FAILURE));
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/signup";
        }
        redirectAttributes
                .addFlashAttribute("flash", new FlashMessage("Registration successful!", FlashMessage.Status.SUCCESS));
        return "redirect:/login";
    }

    @RequestMapping("/profile")
    public String userProfile(Model model, Principal principal) {

        User user = getUser( (UsernamePasswordAuthenticationToken) principal);
        if(user == null){
            return "redirect:/login";
        }

        List<Recipe> recipes = new ArrayList<>();
        recipes.addAll(user.getFavorites());

        model.addAttribute("user", user);
        model.addAttribute("recipes", recipes);

        return "profile";
    }

    private User getUser(UsernamePasswordAuthenticationToken principal){
        try{
            return userService.findByUsername(((CustomUserDetails) principal.getPrincipal()).getUsername());
        } catch (NullPointerException e) {
            System.out.println("ERROR: User not found.");
        }
        return null;
    }
}
