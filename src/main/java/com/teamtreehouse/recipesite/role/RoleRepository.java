package com.teamtreehouse.recipesite.role;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
}
