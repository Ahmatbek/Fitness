package kg.biamino.projects.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${application.domain}")
    private String myDomain;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter,
                                                   JwtAuthenticationManager jwtAuthenticationManager,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        AuthenticationFilter authenticationManager = new AuthenticationFilter(jwtAuthenticationManager, jwtAuthenticationConverter);
        authenticationManager.setSuccessHandler((request, response, authentication) -> {});



         http
                 .cors(cors-> cors.configurationSource(corsConfigurationSource))
                 .addFilterBefore(authenticationManager, UsernamePasswordAuthenticationFilter.class)
                 .csrf(AbstractHttpConfigurer::disable)
                 .authorizeHttpRequests(request ->
                         request.requestMatchers("/auth/login", "/auth/logout").permitAll()
                                 .requestMatchers(HttpMethod.POST, "/trainees").permitAll()
                                 .requestMatchers(HttpMethod.POST, "/trainers").permitAll()
                                 .requestMatchers("/trainees/**").hasAuthority("TRAINEE")
                                 .requestMatchers("/trainers/**").hasAuthority("TRAINER")
                                 .anyRequest().authenticated()

                 )
                 .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


         return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8080",myDomain));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Length"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }



}
