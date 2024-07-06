package com.smirnov.carwashspring.security;

import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginAndDeletedIsFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
                user.getRole().getRolesUser().name()
        );
        log.info("Получен user с login: {}. Роль: {}", user.getLogin(), grantedAuthority.getAuthority());
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), Set.of(grantedAuthority));
    }
}
