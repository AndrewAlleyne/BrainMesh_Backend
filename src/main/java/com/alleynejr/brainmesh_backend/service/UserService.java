package com.alleynejr.brainmesh_backend.service;


import com.alleynejr.brainmesh_backend.exceptions.UserAlreadyExistException;
import com.alleynejr.brainmesh_backend.model.LoginRequest;
import com.alleynejr.brainmesh_backend.model.RegistrationRequest;
import com.alleynejr.brainmesh_backend.model.User;
import com.alleynejr.brainmesh_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String register(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        Optional<User> byUsername = userRepository.findByUsername(registrationRequest.getUsername());

        if (byUsername.isPresent()) {
            throw new UserAlreadyExistException("User exist in database while trying to register!");
        }

        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    public String login(LoginRequest loginRequest) {
        User user = new User();

        user.setUsername(loginRequest.getUsername());
        user.setPassword(loginRequest.getPassword());

        Optional<User> byUsername = userRepository.findByUsername(loginRequest.getUsername());

        if (byUsername.isEmpty()) {
            throw new UsernameNotFoundException("User not found while trying to login");
        }


        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        Authentication authenticationObject = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        System.out.println(authenticationObject.isAuthenticated());

        return jwtService.generateToken(user);
    }
}
