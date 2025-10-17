package com.mountblue.io.BlogApplication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private MyUserDetailsService userDetailsService;

    public SecurityConfig(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(configure ->
                        configure
                                .requestMatchers(HttpMethod.POST, "/post/save")
                                .hasAnyRole("AUTHOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/post/*/update", "/post/*/delete")
                                .hasAnyRole("AUTHOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST,
                                        "/post/*/comments/*/edit", "/post/*/comments/*/delete")
                                .hasAnyRole("AUTHOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/post/*/comments/*/edit")
                                .hasAnyRole("AUTHOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET,
                                        "/post", "/post/", "/post/*", "/post/**", "/post/search/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/post/*/comments", "/post/*/comments/")
                                .permitAll()
                                .requestMatchers("/login", "/register").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/post", true)
                                .failureUrl("/login?error")
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/post")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
