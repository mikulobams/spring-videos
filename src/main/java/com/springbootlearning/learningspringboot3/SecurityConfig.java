package com.springbootlearning.learningspringboot3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
        userDetailsManager.createUser(User.withDefaultPasswordEncoder().username("admin").password("password").roles("ADMIN").build());
        return userDetailsManager;
    }

    @Bean
    CommandLineRunner initUsers(UserManagementRepository repository) {
        return args -> {
            repository.save(new UserAccount("user", "password", "ROLE_USER"));
            repository.save(new UserAccount("admin", "password", "ROLE_ADMIN"));
        };
    }

    @Bean
    UserDetailsService userService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .asUser();
    }

    @Bean
    SecurityFilterChain configureSecurity(HttpSecurity http)
            throws Exception {
        http.authorizeHttpRequests() //
                .requestMatchers("/login").permitAll() //
                .requestMatchers("/", "/search").authenticated() //
                .requestMatchers(HttpMethod.GET, "/api/**")
                .authenticated() //
                .requestMatchers(HttpMethod.POST, "/new-video",
                        "/api/**").hasRole("ADMIN") //
                .anyRequest().denyAll() //
                .and() //
                .formLogin() //
                .and() //
                .httpBasic();
        return http.build();
    }
}