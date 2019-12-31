package com.teamtreehouse.recipesite.service;

import com.teamtreehouse.recipesite.role.Role;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserRepository;
import com.teamtreehouse.recipesite.user.UserService;
import com.teamtreehouse.recipesite.user.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService = new UserServiceImpl();

    @Test
    public void findByUsername_ShouldReturnOneUser() throws Exception {
        when(userRepository.findByUsername("user10")).thenReturn(new User());
        assertThat(userService.findByUsername("user10"), instanceOf(User.class));
        verify(userRepository).findByUsername("user10");
    }

    @Test
    public void findAll_shouldReturnTwoUsers() throws Exception {
        User userRole = new User("userRole", "password", new Role("ROLE_USER"));
        userRole.setId(1L);
        User adminRole = new User("adminRole", "password", new Role("ROLE_ADMIN"));
        adminRole.setId(2L);
        List<User> users = new ArrayList<>();
        users.add(userRole);
        users.add(adminRole);

        when(userRepository.findAll()).thenReturn(users);
        assertEquals("findAll should return two users", 2, userService.findAll().size());
        verify(userRepository).findAll();
    }

    @Test
    public void save_shouldVerifySavedUser() throws Exception {
        User user = new User("userRole", "password", new Role("ROLE_USER"));
        user.setId(1L);

        when(userRepository.save(user)).thenReturn(user);
        User result = userService.save(user);

        assertEquals(1, result.getId().intValue());
        assertEquals("userRole", result.getUsername());
        assertEquals("ROLE_USER", result.getRole().getName());
        verify(userRepository).save(user);
    }
}
