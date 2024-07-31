package com.smirnov.carwashspring.service.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import com.smirnov.carwashspring.exception.JWTValidException;
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
 * Сервисный слой фильтров.
 */
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtSecurityService jwtSecurityService;

    private final UserService userService;

    /**
     * Проверяет, имеет ли запрос валидный Bearer-токен
     * @param request Запрос
     * @param response Ответ
     * @param filterChain Фильтр
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String substringHeader = "Bearer";
        if (authHeader == null || !authHeader.startsWith(substringHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.replace(substringHeader, "").replace(" ", "");
        String userName;
        try {
            userName = jwtSecurityService.getSubject(jwt);
        } catch (BadJOSEException | ParseException | JOSEException e) {
            throw new JWTValidException("Не удалось извлечь токен");
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
}
