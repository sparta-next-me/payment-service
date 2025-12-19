package org.nextme.payment_service.payment.infrastructure.security;

/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;*/


import org.nextme.common.jwt.JwtTokenProvider;
import org.nextme.common.jwt.TokenBlacklistService;
import org.nextme.common.security.DirectJwtAuthenticationFilter;
import org.nextme.common.security.GatewayUserHeaderAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public GatewayUserHeaderAuthenticationFilter gatewayUserHeaderAuthenticationFilter() {
        return new GatewayUserHeaderAuthenticationFilter();
    }

    @Bean
    public DirectJwtAuthenticationFilter directJwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            TokenBlacklistService tokenBlacklistService
    ) {
        return new DirectJwtAuthenticationFilter(
                jwtTokenProvider,
                tokenBlacklistService,
                List.of()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            GatewayUserHeaderAuthenticationFilter gatewayUserHeaderAuthenticationFilter,
            DirectJwtAuthenticationFilter directJwtAuthenticationFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/payment.html", "/index.html", "/public/**").permitAll()
                        .requestMatchers("/health", "/public/**").permitAll()
                        .requestMatchers("/actuator/prometheus", "/actuator/health").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api-docs.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayUserHeaderAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(directJwtAuthenticationFilter,
                        GatewayUserHeaderAuthenticationFilter.class);
        return http.build();
    }
}

/*import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public GatewayUserHeaderAuthenticationFilter gatewayUserHeaderAuthenticationFilter() {
        return new GatewayUserHeaderAuthenticationFilter();
    }

    @Bean
    public DirectJwtAuthenticationFilter directJwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            TokenBlacklistService tokenBlacklistService
    ) {
        return new DirectJwtAuthenticationFilter(
                jwtTokenProvider,
                tokenBlacklistService,
                List.of()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            GatewayUserHeaderAuthenticationFilter gatewayUserHeaderAuthenticationFilter,
            DirectJwtAuthenticationFilter directJwtAuthenticationFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/public/**").permitAll()
                        .requestMatchers("/v1/advisor/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayUserHeaderAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(directJwtAuthenticationFilter,
                        GatewayUserHeaderAuthenticationFilter.class);
        return http.build();
    }
}*/
/*@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .authorizeHttpRequests(auth ->
                auth.requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().permitAll());

       http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        return http.build();
    }
}*/
