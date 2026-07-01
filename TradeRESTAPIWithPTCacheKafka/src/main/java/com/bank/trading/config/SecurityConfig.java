package com.bank.trading.config;

//import com.bank.trading.security.CounterpartyValidationFilter;
import com.bank.trading.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

//    @Autowired
//    private CounterpartyValidationFilter filter;

    @Bean
    SecurityFilterChain securityFilterChain( HttpSecurity http
                                            ) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())


                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/**",
                                "/actuator/**",
                                "/ws/**",
                                "/api/v1/audit/**")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/recon/breaks")
                        .hasAnyRole("OPS", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/recon/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/recon/**")
                        .hasAnyRole("OPS", "ADMIN")

                        .requestMatchers(
                                "/api/v1/trades/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/v1/instruments/**")
                        .hasAnyRole("OPS", "ADMIN")

                        .anyRequest()
                        .authenticated())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

//                .addFilterBefore(
//                        filter,
//                        UsernamePasswordAuthenticationFilter.class);
//                .httpBasic(Customizer.withDefaults());   not required

        return http.build();
    }

    @Bean
    UserDetailsService users() {

        UserDetails ops =
                User.withUsername("ops")
                        .password("{noop}ops123")
                        .roles("OPS")
                        .build();

        UserDetails admin =
                User.withUsername("admin")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(
                ops,
                admin);
    }


    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }
}

/*

| Endpoint                       | ROLE_OPS | ROLE_ADMIN |
| ------------------------------ | -------- | ---------- |
| GET /recon/breaks              | ✅        | ✅          |
| GET /recon/breaks/{id}         | ✅        | ✅          |
| POST /recon/breaks             | ✅        | ✅          |
| PUT /recon/breaks/{id}/resolve | ❌        | ✅          |

 */