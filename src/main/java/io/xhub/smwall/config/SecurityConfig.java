package io.xhub.smwall.config;

import io.xhub.smwall.constants.ApiPaths;
import io.xhub.smwall.constants.WebSocketPaths;
import io.xhub.smwall.security.jwt.JwtFilter;
import io.xhub.smwall.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final String[] WHITE_LIST = {
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/h2/**",
            "/login",
            "/config",
            "/ws/**"
    };

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, ApiPaths.V1 + ApiPaths.AUTH + ApiPaths.LOGIN).permitAll()
                .requestMatchers(HttpMethod.PATCH, ApiPaths.V1 + ApiPaths.AUTH + ApiPaths.UPDATE_PASSWORD).permitAll()
                .requestMatchers(HttpMethod.POST, ApiPaths.V1 + ApiPaths.USERS + ApiPaths.SIGNUP).permitAll()
                .requestMatchers(HttpMethod.POST, ApiPaths.V1 + ApiPaths.USERS + ApiPaths.SIGNED_UP + "/**").permitAll()
                .requestMatchers(HttpMethod.POST, ApiPaths.V1 + ApiPaths.USERS + ApiPaths.RESET_PASSWORD + "/**").permitAll()
                .requestMatchers(HttpMethod.GET, ApiPaths.V1 + ApiPaths.HEADER).permitAll()
                .requestMatchers(HttpMethod.GET, ApiPaths.V1 + ApiPaths.FOOTER).permitAll()
                .requestMatchers(HttpMethod.GET, ApiPaths.V1 + ApiPaths.ANNOUNCEMENTS).permitAll()
                .requestMatchers(HttpMethod.GET, ApiPaths.V1 + ApiPaths.MEDIA).permitAll()
                .requestMatchers(HttpMethod.GET, ApiPaths.V1 + ApiPaths.WALL + ApiPaths.SETTINGS + ApiPaths.LATEST).permitAll()
                .requestMatchers(HttpMethod.GET, ApiPaths.ANNOUNCEMENTS + WebSocketPaths.WS).permitAll()
                .requestMatchers(ApiPaths.V1 + ApiPaths.WEBHOOKS + "/**").permitAll()
                .requestMatchers(WHITE_LIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(jwtTokenProvider);
        return authenticationManagerBuilder.build();
    }
}