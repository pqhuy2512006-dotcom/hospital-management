package com.nhom12.hospital.config;

import com.nhom12.hospital.security.JwtCookieBearerTokenResolver;
import com.nhom12.hospital.security.AccountStatusJwtValidator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecretKey jwtSecretKey(@Value("${app.security.jwt.secret:}") String encodedSecret) {
        if (encodedSecret == null || encodedSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured with a Base64 secret of at least 32 bytes.");
        }

        byte[] secret = Base64.getDecoder().decode(encodedSecret);
        if (secret.length < 32) {
            throw new IllegalStateException("JWT_SECRET must decode to at least 32 bytes for HS256.");
        }
        return new SecretKeySpec(secret, "HmacSHA256");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
        public JwtDecoder jwtDecoder(
            SecretKey jwtSecretKey,
            AccountStatusJwtValidator accountStatusJwtValidator) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer("https://hospital-management.local"),
            accountStatusJwtValidator));
        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository(
            @Value("${app.security.cookie.secure:false}") boolean secureCookie) {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> cookie
                .path("/")
                .sameSite("Strict")
                .httpOnly(true)
                .secure(secureCookie));
        return repository;
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> pageAuthorizationManager() {
        return (authentication, context) -> {
            Authentication currentUser = authentication.get();
            if (currentUser == null || !currentUser.isAuthenticated()
                    || currentUser instanceof AnonymousAuthenticationToken) {
                return new AuthorizationDecision(false);
            }

            String path = context.getRequest().getRequestURI();
            String page = path.substring(path.lastIndexOf('/') + 1);
            boolean allowed = currentUser.getAuthorities().stream().anyMatch(authority ->
                    Arrays.stream(com.nhom12.hospital.security.Role.values())
                            .anyMatch(role -> role.getValue().equals(authority.getAuthority())
                                    && role.canAccess(page)));
            return new AuthorizationDecision(allowed);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthorizationManager<RequestAuthorizationContext> pageAuthorizationManager,
            JwtCookieBearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            CookieCsrfTokenRepository csrfTokenRepository) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = (request, response, error) -> {
            if (request.getRequestURI().startsWith("/api/")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                response.sendRedirect("/login.html");
            }
        };
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository)
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/login.html",
                            "/api/v1/auth/login",
                            "/api/v1/auth/register",
                            "/api/v1/auth/logout",
                            "/api/v1/auth/csrf",
                            "/auth-guard.js",
                            "/error",
                            "/favicon.ico",
                            "/*.css",
                            "/**/*.css",
                            "/*.js",
                            "/**/*.js").permitAll()
                    .requestMatchers("/*.html", "/**/*.html").access(pageAuthorizationManager)
                    .requestMatchers("/api/v1/**").authenticated()
                    .anyRequest().authenticated())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .oauth2ResourceServer(resourceServer -> resourceServer
                    .bearerTokenResolver(bearerTokenResolver)
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }
}