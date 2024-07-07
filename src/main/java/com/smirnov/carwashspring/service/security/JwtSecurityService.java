package com.smirnov.carwashspring.service.security;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.smirnov.carwashspring.entity.users.RolesUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

/**
 * Сервисный слой для генерации токена/
 */
@Service
@RequiredArgsConstructor
public class JwtSecurityService {

    private final WebApplicationContext webApplicationContext;

    //@Value("${security.jwtSecret}")
    private String jwtSecret = "841D8A6C80CBA4FCAD32D5367C18C53B";
    //@Value("${security.jwtSecretExpiration}")
    private long jwtSecretExpiration = 86400000;

    @Bean("JWEEncrypter")
    public JWEEncrypter encrypter() throws KeyLengthException {
        return new DirectEncrypter(jwtSecret.getBytes());
    }

    @Bean("JWTProcessor")
    public ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor() {
        ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SimpleSecurityContext> jweKeySource = new ImmutableSecret<>(jwtSecret.getBytes());
        JWEKeySelector<SimpleSecurityContext> jweKeySelector =
                new JWEDecryptionKeySelector<>(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256, jweKeySource);
        jwtProcessor.setJWEKeySelector(jweKeySelector);
        return jwtProcessor;
    }

    /**
     * Генерирует токен для обновления
     *
     * @return Имя токена
     */
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Возвращает сгенерированный токен
     *
     * @param user Пользователь в контексте Spring
     * @return Токен
     * @throws JOSEException
     */
    public String generateToken(UserDetailsCustom user) throws JOSEException {
        System.out.println(user.getAuthorities());
        System.out.println(user.getRolesUser());
        System.out.println(user.getId());
        JWEObject jweObject = new JWEObject(
                new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256),
                new Payload(
                        new JWTClaimsSet.Builder()
                                .subject(user.getUsername()) // идентификатор пользователя subject
                                .claim("id", user.getId())
                                .claim("roles", user.getRolesUser())
                                .issueTime(new Date()) // время выдачи токена
                                .expirationTime(new Date(System.currentTimeMillis() + jwtSecretExpiration * 10000))
                                .build().toJSONObject()));
        jweObject.encrypt((JWEEncrypter) webApplicationContext.getBean("JWEEncrypter"));
        return jweObject.serialize();
    }

    /**
     * Извлекает токен.
     *
     * @param token JW-токе
     * @return
     * @throws BadJOSEException
     * @throws ParseException
     * @throws JOSEException
     */
    private JWTClaimsSet extractClaims(String token) throws BadJOSEException, ParseException, JOSEException {
        ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor =
                (ConfigurableJWTProcessor<SimpleSecurityContext>) webApplicationContext.getBean("JWTProcessor");
        return jwtProcessor.process(token, null);
    }

    /**
     * Извлекает логин пользователя из токена
     *
     * @param token Токен
     * @return Логин
     * @throws BadJOSEException
     * @throws ParseException
     * @throws JOSEException
     */
    public String getSubject(String token) throws BadJOSEException, ParseException, JOSEException {
        JWTClaimsSet claims = extractClaims(token);
        return claims.getSubject();
    }

    public RolesUser getRoles(String token) throws BadJOSEException, ParseException, JOSEException {
        JWTClaimsSet claims = extractClaims(token);
        return (RolesUser) claims.getClaim("roles");
    }

    public Integer getId(String token) throws BadJOSEException, ParseException, JOSEException {
        JWTClaimsSet claims = extractClaims(token);
        return (Integer) claims.getClaim("id");
    }

    /**
     * Проверяет токен на валидность.
     *
     * @param token       Токен
     * @param userDetails Пользователь в контексте Spring
     * @return true/false, если токен валидный/не валидный
     * @throws BadJOSEException
     * @throws ParseException
     * @throws JOSEException
     */
    public boolean isTokenValid(String token, UserDetails userDetails) throws BadJOSEException, ParseException, JOSEException {
        String userName = getSubject(token);
        Date expiration = extractClaims(token).getExpirationTime();
        return userName.equals(userDetails.getUsername()) && expiration.after(new Date());
    }
}
