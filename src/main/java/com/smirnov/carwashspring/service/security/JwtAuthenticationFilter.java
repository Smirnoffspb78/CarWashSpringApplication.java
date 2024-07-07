package com.smirnov.carwashspring.service.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.smirnov.carwashspring.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

/**
 * Фильтр для обработки запросов.
 */
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtSecurityService jwtSecurityService;

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, // запрос
                                    HttpServletResponse response, // ответ
                                    FilterChain filterChain // следующий фильтр
    ) throws ServletException, IOException {
        // если запрос содержит заголовок Authorization и текст начинается с Bearer
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        // извлекаем и валидируем токен
        String jwt = authHeader.substring(7);
        String userName = null;
        try {
            userName = jwtSecurityService.getSubject(jwt);
        } catch (BadJOSEException | ParseException | JOSEException e) {
            throw new IOException(e);
        }
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetailsCustom userDetails = userService.loadUserByUsername(userName);
            try {
                if (jwtSecurityService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (BadJOSEException | ParseException | JOSEException e) {
                throw new IOException(e);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Возвращает аутентифицированного пользователя.
     * @return аутентифицированный пользователь.
     */
    public UserDetailsCustom getAuthUser(){
        return  (UserDetailsCustom) SecurityContextHolder.getContextHolderStrategy()
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
