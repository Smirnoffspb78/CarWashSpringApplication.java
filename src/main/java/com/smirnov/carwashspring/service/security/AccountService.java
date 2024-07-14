package com.smirnov.carwashspring.service.security;

import com.nimbusds.jose.JOSEException;
import com.smirnov.carwashspring.dto.request.create.UserCreateDTO;
import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.dto.response.get.Token;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtSecurityService jwtSecurityService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    /**
     * Регистрирует пользователя и кодирует пароль, введенный при регистрации.
     * @param userCreateDTO Пользователь
     */
    public Integer registration(UserCreateDTO userCreateDTO) {
        userService.checkUserByLogin(userCreateDTO.login());
        User user = modelMapper.map(userCreateDTO, User.class);
        user.setRole(new Role(RolesUser.ROLE_USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Integer id =  userService.saveUser(user);
        log.info("Зарегистрирован пользователь с id: {}", id);
        return id;
    }

    /**
     * Авторизует пользователя по логину и паролю
     * @param login Логин
     * @param password Пароль
     * @return Токен
     * @throws AccountException Если введены некорректные данные логина или пароля
     */
    public Token loginAccount(String login, String password) throws AccountException {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Token token = new Token();
        try {
            token.setAccessToken(jwtSecurityService.generateToken((UserDetailsCustom) authentication.getPrincipal()));
            token.setRefreshToken(jwtSecurityService.generateRefreshToken());
        } catch (JOSEException e) {
            log.error("Не удалось создать токен");
            throw new AccountException("Token cannot not created: " + e.getMessage());
        }
        return token;
    }
}
