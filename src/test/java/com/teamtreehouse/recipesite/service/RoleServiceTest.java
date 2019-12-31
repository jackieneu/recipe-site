package com.teamtreehouse.recipesite.service;


import com.teamtreehouse.recipesite.recipe.Recipe;
import com.teamtreehouse.recipesite.role.Role;
import com.teamtreehouse.recipesite.role.RoleRepository;
import com.teamtreehouse.recipesite.role.RoleService;
import com.teamtreehouse.recipesite.role.RoleServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService = new RoleServiceImpl();

    @Test
    public void findByName_ShouldReturnOne() throws Exception {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(new Role());
        assertThat(roleService.findByName("ROLE_USER"), instanceOf(Role.class));
        verify(roleRepository).findByName("ROLE_USER");
    }
}
