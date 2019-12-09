package com.teamtreehouse.recipesite.web.controller;

import com.teamtreehouse.recipesite.ingredient.Ingredient;
import com.teamtreehouse.recipesite.ingredient.IngredientService;
import com.teamtreehouse.recipesite.recipe.Recipe;
import com.teamtreehouse.recipesite.recipe.RecipeService;
import com.teamtreehouse.recipesite.user.CustomUserDetails;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserService;
import com.teamtreehouse.recipesite.web.Category;
import com.teamtreehouse.recipesite.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/index", "/recipe", "/"}, method = RequestMethod.GET)
    public String login(
            @RequestParam(name="category", required=false) String category,
            Model model, Principal principal) {

        User user = getUser( (UsernamePasswordAuthenticationToken) principal);

        List<Recipe> recipes;
        if(category == null || category.equals("ALL CATEGORIES")){
            recipes = recipeService.findAll();
        } else {
            //TODO: The repo should do this filter
            recipes = recipeService.findAll().stream().filter(recipe -> recipe.getCategory().getName().equalsIgnoreCase(category)).collect(Collectors.toList());
        }

        model.addAttribute("recipes", recipes);
        model.addAttribute("categories", Category.values());
        model.addAttribute("lastSelected", category);
        if(user != null){
            model.addAttribute("user", user);
        }
        return "index";
    }

    @RequestMapping(value = "/recipe/detail/{recipeId}", method = RequestMethod.GET)
    public String detail(@PathVariable Long recipeId, Model model, Principal principal) {
        if(!model.containsAttribute("recipe")) {
            model.addAttribute("recipe", recipeService.findById(recipeId));
        }
        User user = getUser((UsernamePasswordAuthenticationToken) principal);

        if (user != null) {
            model.addAttribute("user", user);
        }
        return "detail";
    }

    @RequestMapping(value = "recipe/save", method = RequestMethod.GET)
    public String saveRecipe(Model model, HttpServletRequest request, Principal principal) {
        if (!model.containsAttribute("recipe")) {
            Recipe recipe = new Recipe();
            recipe.addIngredient(new Ingredient("", "", 0));
            recipe.addInstruction("");
            model.addAttribute("recipe", recipe);
        }
        User user = getUser((UsernamePasswordAuthenticationToken) principal);

        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("action", "/recipe/save");
        model.addAttribute("categories", Category.values());
        model.addAttribute("cancel", String.format("%s", request.getHeader("referer")));

        return "edit";
    }

    @RequestMapping(value = "recipe/save", method = RequestMethod.POST)
    public String saveRecipe(@Valid Recipe recipe, BindingResult result, RedirectAttributes redirectAttributes) {
        //Check instructions and ingredients
        List<String> interimInstructions = recipe.getInstructions().stream().filter(instruction -> !instruction.trim().isEmpty()).collect(Collectors.toList());
        List<Ingredient> interimIngredients = recipe.getIngredients().stream().filter( ingredient -> {
            return ( (recipe.getIngredients().size() == 1
                    && (ingredient.getCondition().trim().isEmpty()
                        || ingredient.getItem().trim().isEmpty()
                        || ingredient.getQuantity() <=0))
                    ||
                    (recipe.getInstructions().size() > 1 &&
                            ((ingredient.getCondition().trim().isEmpty()
                                    || ingredient.getItem().trim().isEmpty()
                                    || ingredient.getQuantity() <= 0)
                            && !(ingredient.getCondition().trim().isEmpty()
                                    && ingredient.getItem().trim().isEmpty()
                                    && ingredient.getQuantity() == 0))));
        }).collect(Collectors.toList());

        // Add recipe if valid data was received
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid input. Please try again.", FlashMessage.Status.FAILURE));
            System.out.println("ERROR");
            result.getFieldErrors().forEach(error -> System.out.println(error));
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.recipe", result);
            redirectAttributes.addFlashAttribute("recipe", recipe);

            if(interimInstructions.size() == 0){
                redirectAttributes.addFlashAttribute("instructionError", "error");
            }
            if(interimIngredients.size() > 0){
                redirectAttributes.addFlashAttribute("ingredientError", "error");
            }
            return "redirect:/recipe/save";
        } else {
            if(interimInstructions.size() == 0){
                redirectAttributes.addFlashAttribute("instructionError", "error");
            }
            if(interimIngredients.size() > 0){
                redirectAttributes.addFlashAttribute("ingredientError", "error");
            }
            if(interimInstructions.size() == 0 || interimIngredients.size() > 0){
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.recipe", result);
                redirectAttributes.addFlashAttribute("recipe", recipe);
                return "redirect:/recipe/save";
            }

            Category category = recipe.getCategory();
            if (category != null) {
                recipe.setCategory(category);
            }
//            User user = getCurrentLoggedInUser();
            List<Ingredient> finalIngredients = recipe.getIngredients().stream().filter(ingredient -> {
                return !(ingredient.getCondition().trim().isEmpty()
                        && ingredient.getItem().trim().isEmpty()
                        && ingredient.getQuantity() <= 0);
            }).collect(Collectors.toList());

            recipe.setIngredients(finalIngredients);
            ingredientService.save(finalIngredients);

            recipe.setInstructions(interimInstructions);
//            recipe.setUser(user);
            recipeService.save(recipe);
//            user.addCreatedRecipe(recipe);
//            userService.save(user);
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Recipe saved!", FlashMessage.Status.SUCCESS));
        }

        // Redirect browser to home page
//        return String.format("redirect:/recipe/detail/%s", recipe.getId());
        return String.format("redirect:/recipe");
    }

    @RequestMapping(value="/recipe/edit/{recipeId}", method = RequestMethod.GET)
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String editRecipe(@PathVariable Long recipeId, Model model, Principal principal, HttpServletRequest request) {
        if (!model.containsAttribute("recipe")) {
            Recipe recipe = recipeService.findById(recipeId);
            model.addAttribute("recipe", recipe);
        }
        User user = getUser((UsernamePasswordAuthenticationToken) principal);

        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("action", "/recipe/save");
        model.addAttribute("cancel", String.format("%s", request.getHeader("referer")));
        model.addAttribute("categories", Category.values());

        return "edit";
    }

    @RequestMapping(value = "recipe/{recipeId}/favorite", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String toggleFavorite(@PathVariable Long recipeId, HttpServletRequest request, Principal principal) {
        Recipe recipe = recipeService.findById(recipeId);
        User user = getUser((UsernamePasswordAuthenticationToken) principal);
        userService.toggleFavorite(user, recipe);
        userService.save(user);

        return String.format("redirect:%s", request.getHeader("referer"));
    }
    
    @RequestMapping(value = "/recipe/delete/{recipeId}", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String deleteRecipe(@PathVariable Long recipeId, HttpServletRequest request) {
        Recipe recipe = recipeService.findById(recipeId);
        recipeService.delete(recipe);
        return "redirect:/";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String usernameNotFound(Model model, Exception ex) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", "");
        model.addAttribute("error", "");
        return "error";
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
