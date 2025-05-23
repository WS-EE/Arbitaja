package com.arbitaja.backend.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
    @Value("${VITE_APP_BASE_URL}")
    private String VITE_APP_BASE_URL;

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
        corsConfig.setAllowedOrigins(Arrays.asList(VITE_APP_BASE_URL));
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
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());

        authenticationManagerBuilder.parentAuthenticationManager(null);
        return authenticationManagerBuilder.build();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authenticationManager(authenticationManager(http))
                .addFilterBefore(new LoginLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/login-user", "/error", "/user/signup/create", "/swagger-ui/**",
                                "/context-path/**", "/v3/**", "/health", "/competition/criteria/**", "competition/get",
                                "competition/all/get", "/dashboard/**").permitAll()
                        .anyRequest().authenticated()
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginProcessingUrl("/login-user")
                        .defaultSuccessUrl("/login?error=false", true)
                        .failureUrl("/login?error=true")
                )
                .rememberMe(rememberMe -> rememberMe
                        .userDetailsService(userDetailsService())
                        .tokenValiditySeconds(30 * 5)
                        .rememberMeCookieName("remember-me")
                        .rememberMeParameter("rememberMe")
                        .key("somesecret")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
