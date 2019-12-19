package com.teamtreehouse.recipesite.ingredient;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends PagingAndSortingRepository<Ingredient, Long> {

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    Ingredient save(@Param("ingredient") Ingredient ingredient);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    <S extends Ingredient> Iterable<S> saveAll(Iterable<S> ingredients);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    void deleteById(@Param("id") Long id);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    void delete(@Param("ingredient") Ingredient ingredient);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteAll(Iterable<? extends Ingredient> ingredients);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteAll();
}
