package com.alleynejr.brainmesh_backend.config;


import com.alleynejr.brainmesh_backend.config.filters.JWTFilter;
import com.alleynejr.brainmesh_backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    JwtService jwtService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.addFilterBefore(new JWTFilter(customUserDetailsService, jwtService, authenticationManagerBean(http)), UsernamePasswordAuthenticationFilter.class);

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers(antMatcher(HttpMethod.valueOf("/login/**"), "register/**")).permitAll()
                        .requestMatchers(antMatcher("/authenticate/**")).permitAll()
                        .requestMatchers(antMatcher("/login/**")).permitAll()
                        .requestMatchers(antMatcher("/register/**")).permitAll()
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(antMatcher("/error/**")).permitAll()
                        .requestMatchers(antMatcher("/generateToken/**")).permitAll()
                        .requestMatchers(antMatcher("/refreshToken/**")).permitAll()
                        .requestMatchers((antMatcher("/logout/**"))).hasRole("USER")
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/logout").addLogoutHandler(LogoutSuccessHandler())).httpBasic(Customizer.withDefaults()).headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));

        return http.build();
    }

    @Bean
    public LogoutHandler LogoutSuccessHandler() {
        return new JwtLogoutSuccessHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    //Not sure this is valid for my usecase.
    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
