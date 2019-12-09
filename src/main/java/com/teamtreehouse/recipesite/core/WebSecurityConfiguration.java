package com.teamtreehouse.recipesite.core;

import com.teamtreehouse.recipesite.user.CustomUserDetailService;
import com.teamtreehouse.recipesite.user.CustomUserDetails;
import com.teamtreehouse.recipesite.user.User;
import com.teamtreehouse.recipesite.user.UserService;
import com.teamtreehouse.recipesite.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService)
                .passwordEncoder(User.PASSWORD_ENCODER);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/login")
                .and()
                .csrf().disable();
    }

    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) ->
                response.sendRedirect("/recipe");
    }

    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            request.getSession().setAttribute("flash", new FlashMessage("Incorrect username and/or password. Please try again.", FlashMessage.Status.FAILURE
                    )
            );
            response.sendRedirect("/login");
        };
    }
}
