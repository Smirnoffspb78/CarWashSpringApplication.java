package com.smirnov.carwashspring.configuration;

import com.smirnov.carwashspring.entity.users.RolesUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.security.auth.login.AccountException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //Добавляются, для того, чтобы попасть в контекст Секюрити спринг
@EnableMethodSecurity(securedEnabled = true) //Доабавляется только в том случае, если требуется над каждым
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;

    /**
     * AuthenticationProvider - провайдер, которому делегируеся процесс аутентификации
     * DaoAuthenticationProvider - реализация AuthenticationProvider, занимающаяся аутентификацией
     */
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);  // для получения пользователя из хранилища
        provider.setPasswordEncoder(passwordEncoder()); // для работы с паролями
        return provider;
    }

    /**
     * Основа процесса аутентификации spring security
     * Делегирует аутентификацию провайдевам - AuthenticationProvider
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws AccountException {
        try {
            return config.getAuthenticationManager();
        } catch (Exception e) {
            throw new AccountException("AuthenticationManager not configured: " + e.getMessage());
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, "/services")
                        .hasAnyRole("ADMIN")
                        .anyRequest().authenticated()

                )
                .formLogin(withDefaults());
        return http.build();
    }

   @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
   }
}
