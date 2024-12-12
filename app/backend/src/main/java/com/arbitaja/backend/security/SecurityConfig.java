package com.arbitaja.backend.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;


@EnableWebSecurity
@Configuration
public class SecurityConfig{

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);  // Allow semicolons in URLs
        return firewall;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);
        return request -> corsConfig;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService())  // Use your custom userDetailsService
                .passwordEncoder(passwordEncoder());  // Use the password encoder

        return authenticationManagerBuilder.build();  // Build and return the AuthenticationManager
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authenticationManager(authenticationManager(http))
                .addFilterBefore(new LoginLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Set session creation policy
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply the CORS configuration
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/login-user", "/error").permitAll()  // Allow these pages without authentication
                        .anyRequest().authenticated() // Secure other endpoints
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginProcessingUrl("/login-user")
                        .defaultSuccessUrl("/home", true)  // Redirect to homepage on successful login
                        .failureUrl("/login?error=true")  // Redirect to a successful login page
                )
                .rememberMe(rememberMe -> rememberMe // Configure "Remember Me"
                        .userDetailsService(userDetailsService())
                        .alwaysRemember(true) // Always apply "Remember Me"
                        .tokenValiditySeconds(30 * 5) // Token validity: 5 minutes
                        .rememberMeCookieName("mouni") // Custom cookie name
                        .key("somesecret") // Secret key for token generation
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF (consider enabling it if needed)
                .build();
    }
}
