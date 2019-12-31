package com.teamtreehouse.recipesite.user;

import com.teamtreehouse.recipesite.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User domainUser = userRepository.findByUsername(username);
        if(domainUser == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        Role role = domainUser.getRole();

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));

        CustomUserDetails customUserDetail = new CustomUserDetails();
        customUserDetail.setUser(domainUser);
        customUserDetail.setAuthorities(authorities);

        return customUserDetail;
    }
}
