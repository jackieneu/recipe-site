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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String login(
            @RequestParam(name="category", required=false) String category,
            @RequestParam(name="searchTerm", required=false) String searchTerm,
            Model model, Principal principal) {

        User user = getUser( (UsernamePasswordAuthenticationToken) principal);

        List<Recipe> recipes = recipeService.searchAndFilter(category, searchTerm);

        model.addAttribute("recipes", recipes);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("categories", Category.values());
        model.addAttribute("lastSelected", category);
        if(user != null){
            model.addAttribute("user", user);
        }
        return "index";
    }

    @RequestMapping(value = "/recipe/detail/{recipeId}", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
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

    @RequestMapping(value = "recipe/create", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String createRecipe(Model model, HttpServletRequest request, Principal principal) {
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
        model.addAttribute("action", "/recipe/create");
        model.addAttribute("categories", Category.values());
        model.addAttribute("cancel", String.format("%s", request.getHeader("referer")));

        return "edit";
    }

    @RequestMapping(value = "recipe/create", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String createRecipe(@Valid Recipe recipe, BindingResult result, RedirectAttributes redirectAttributes, Principal principal) {
        //Check instructions and ingredients
        List<String> interimInstructions = formatInstructions(recipe);
        List<Ingredient> interimIngredients = formatIngredients(recipe);

        // Add recipe if valid data was received
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid input. Please try again.", FlashMessage.Status.FAILURE));
            System.out.println("ERROR");
            result.getFieldErrors().forEach(error -> System.out.println(error));
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.recipe", result);
            redirectAttributes.addFlashAttribute("recipe", recipe);

            checkErrorForInstructionsOrIngredients(redirectAttributes, interimInstructions, interimIngredients);
            return "redirect:/recipe/create";
        } else {
            checkErrorForInstructionsOrIngredients(redirectAttributes, interimInstructions, interimIngredients);
            if(interimInstructions.size() == 0 || interimIngredients.size() > 0){
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.recipe", result);
                redirectAttributes.addFlashAttribute("recipe", recipe);
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid input. Please try again.", FlashMessage.Status.FAILURE));
                return "redirect:/recipe/create";
            }

            User user = getUser((UsernamePasswordAuthenticationToken) principal);

            List<Ingredient> finalIngredients = recipe.getIngredients().stream().filter(ingredient -> {
                return !(ingredient.getCondition().trim().isEmpty()
                        && ingredient.getItem().trim().isEmpty()
                        && ingredient.getQuantity() <= 0);
            }).collect(Collectors.toList());
            ingredientService.save(finalIngredients);

            Category category = recipe.getCategory();
            if (category != null) {
                recipe.setCategory(category);
            }

            recipe.setIngredients(finalIngredients);
            recipe.setInstructions(interimInstructions);
            recipe.setCreatedBy(user);
            recipeService.save(recipe);

            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Recipe saved!", FlashMessage.Status.SUCCESS));
        }

        return String.format("redirect:/recipe/detail/%s", recipe.getId());
    }

    @RequestMapping(value = "/recipe/edit/{recipeId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN') or @recipeRepository.findById(#recipeId).orElse(null)?.createdBy?.username == authentication.name")
    public String editRecipe(@PathVariable Long recipeId, Model model, HttpServletRequest request, Principal principal) {
        if (!model.containsAttribute("recipe")) {
            Recipe recipe = recipeService.findById(recipeId);
            model.addAttribute("recipe", recipe);
        }
        User user = getUser((UsernamePasswordAuthenticationToken) principal);

        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("action", String.format("/recipe/update/%s", recipeId));
        model.addAttribute("categories", Category.values());
        model.addAttribute("cancel", String.format("%s", request.getHeader("referer")));

        return "edit";
    }

    @RequestMapping(value = "recipe/update/{recipeId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN') or @recipeRepository.findById(#recipeId).orElse(null)?.createdBy?.username == authentication.name")
    public String updateRecipe(@Valid Recipe recipe, @PathVariable Long recipeId, BindingResult result, RedirectAttributes redirectAttributes, Principal principal) {
        //Check instructions and ingredients
        List<String> interimInstructions = formatInstructions(recipe);
        List<Ingredient> interimIngredients = formatIngredients(recipe);

        // Add recipe if valid data was received
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid input. Please try again.", FlashMessage.Status.FAILURE));
            System.out.println("ERROR");
            result.getFieldErrors().forEach(error -> System.out.println(error));
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.recipe", result);
            redirectAttributes.addFlashAttribute("recipe", recipe);

            checkErrorForInstructionsOrIngredients(redirectAttributes, interimInstructions, interimIngredients);
            return String.format("redirect:/recipe/edit/%s", recipeId);
        } else {
            checkErrorForInstructionsOrIngredients(redirectAttributes, interimInstructions, interimIngredients);
            if(interimInstructions.size() == 0 || interimIngredients.size() > 0){
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.recipe", result);
                redirectAttributes.addFlashAttribute("recipe", recipe);
                redirectAttributes.addFlashAttribute("flash", new FlashMessage("Invalid input. Please try again.", FlashMessage.Status.FAILURE));
                return String.format("redirect:/recipe/edit/%s", recipeId);
            }

            User user = getUser((UsernamePasswordAuthenticationToken) principal);

            List<Ingredient> finalIngredients = recipe.getIngredients().stream().filter(ingredient -> {
                return !(ingredient.getCondition().trim().isEmpty()
                        && ingredient.getItem().trim().isEmpty()
                        && ingredient.getQuantity() <= 0);
            }).collect(Collectors.toList());
            ingredientService.save(finalIngredients);

            Category category = recipe.getCategory();
            if (category != null) {
                recipe.setCategory(category);
            }

            recipe.setIngredients(finalIngredients);
            recipe.setInstructions(interimInstructions);
            recipe.setCreatedBy(user);
            recipeService.save(recipe);

            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Recipe updated!", FlashMessage.Status.SUCCESS));
        }

        return String.format("redirect:/recipe/detail/%s", recipe.getId());
    }

    @RequestMapping(value = "recipe/favorite/{recipeId}", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String toggleFavorite(@PathVariable Long recipeId, HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        Recipe recipe = recipeService.findById(recipeId);
        User user = getUser((UsernamePasswordAuthenticationToken) principal);
        boolean isFavorite = userService.toggleFavorite(user, recipe);
        userService.save(user);

        if(isFavorite){
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Recipe saved as favorite!", FlashMessage.Status.SUCCESS));
        }else{
            redirectAttributes.addFlashAttribute("flash", new FlashMessage("Recipe removed from favorites!", FlashMessage.Status.SUCCESS));
        }

        return String.format("redirect:%s", request.getHeader("referer"));
    }

    @RequestMapping(value = "/recipe/delete/{recipeId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN') or @recipeRepository.findById(#recipeId).orElse(null)?.createdBy?.username == authentication.name")
    public String deleteRecipe(@PathVariable Long recipeId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Recipe recipe = recipeService.findById(recipeId);
        recipeService.clearFavorites(recipe);
        recipeService.delete(recipe);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Recipe deleted!", FlashMessage.Status.SUCCESS));

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

    private void checkErrorForInstructionsOrIngredients(RedirectAttributes redirectAttributes, List<String> interimInstructions, List<Ingredient> interimIngredients) {
        if (interimInstructions.size() == 0) {
            redirectAttributes.addFlashAttribute("instructionError", "error");
        }
        if (interimIngredients.size() > 0) {
            redirectAttributes.addFlashAttribute("ingredientError", "error");
        }
    }

    private List<Ingredient> formatIngredients(@Valid Recipe recipe) {
        return recipe.getIngredients().stream().filter( ingredient -> {
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
    }

    private List<String> formatInstructions(@Valid Recipe recipe) {
        return recipe.getInstructions().stream().filter(instruction -> !instruction.trim().isEmpty()).collect(Collectors.toList());
    }

}
