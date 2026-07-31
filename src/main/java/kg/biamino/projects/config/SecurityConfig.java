package kg.biamino.projects.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter,
                                                   JwtAuthenticationManager jwtAuthenticationManager) throws Exception {
        AuthenticationFilter authenticationManager = new AuthenticationFilter(jwtAuthenticationManager, jwtAuthenticationConverter);
        authenticationManager.setSuccessHandler((request, response, authentication) -> {});



         http
                 .addFilterBefore(authenticationManager, UsernamePasswordAuthenticationFilter.class)
                 .csrf(AbstractHttpConfigurer::disable)
                 .authorizeHttpRequests(request ->
                         request.requestMatchers("/auth/login").permitAll()
                                 .anyRequest().authenticated()
                 )
                 .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .logout(form-> form
                        .logoutSuccessUrl("/")
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .permitAll()

                 );


         return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
