package com.alleynejr.brainmesh_backend.config;

import com.alleynejr.brainmesh_backend.model.User;
import com.alleynejr.brainmesh_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;

    public CustomUserDetailsService() {
    }

    private static boolean isMatches(String username) {
        return username.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n");
    }

    /*
     * User can log in using either a UNIQUE username or email address. */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user;

        try {
            if (isMatches(username)) {
                user = userRepository.findByEmail(username);
            } else {
                user = userRepository.findByUsername(username);
            }

        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Username not found! ");
        }
        User savedUser = user.get();
        Collection<? extends GrantedAuthority> authorityCollection = getAuthorities(savedUser);

        return new org.springframework.security.core.userdetails.User(
                savedUser.getUsername(),
                savedUser.getPassword(),
                authorityCollection
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserDetails user) {
        return user.getAuthorities();
    }
}
