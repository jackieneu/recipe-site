package com.teamtreehouse.recipesite.web.controller;

import com.teamtreehouse.recipesite.role.Role;
import com.teamtreehouse.recipesite.role.RoleService;
import com.teamtreehouse.recipesite.user.CustomUserDetails;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserService;
import com.teamtreehouse.recipesite.web.FlashMessage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController controller;

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Before
    public void setup(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .addFilter(new SecurityContextPersistenceFilter())
                .build();
    }

    @Test
    public void login_shouldRenderLoginView() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"));
    }

    @Test
    public void signup_shouldRenderSignupView() throws Exception{

        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("signup"));
    }

    @Test
    public void signup_shouldRedirectToLogin() throws Exception{
        doAnswer(invocation -> {
            User user = (User)invocation.getArguments()[0];
            user.setId(1L);
            return null;
        }).when(userService).save(any(User.class));

        mockMvc.perform(
                post("/signup")
                        .param("username", "user10")
                        .param("password", "password"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.SUCCESS))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService).save(any(User.class));
    }

    @Test
    public void signup_returnErrorIfUsernameExists() throws Exception{
        when(userService.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(
                post("/signup")
                        .param("username", "user10")
                        .param("password", "password"))
                .andExpect(flash().attribute("flash",
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.FAILURE))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"));
        verify(userService).save(any(User.class));
    }


    @Test
    @WithMockUser(username = "user10", password = "pwd", roles = "ROLE_USER")
    public void profile_shouldRenderUserProfileView() throws Exception {
        User user = new User("user10", "password", new Role("ROLE_USER"));

        when(userService.findByUsername("user10")).thenReturn(user);
        when(principal.getPrincipal()).thenReturn(new CustomUserDetails(user));

        mockMvc.perform(get("/profile").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void profile_redirectToLoginPageIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
