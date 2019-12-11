package com.teamtreehouse.recipesite.recipe;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends PagingAndSortingRepository<Recipe, Long> {


    @RestResource(rel = "name-starts-with", path = "nameStartsWith")
    List<Recipe> findByNameStartsWith(@Param("name") String name);

    @Override
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    Recipe save(Recipe recipe);

    @Override
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Recipe> Iterable<S> saveAll(Iterable<S> recipes);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or @recipeRepository.findOne(#id)?.createdBy?.username == authentication.name")
    void deleteById(@Param("id") Long id);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or #recipe.createdBy?.username == authentication.name")
    void delete(@Param("recipe") Recipe recipe);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteAll(Iterable<? extends Recipe> recipes);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteAll();
}
