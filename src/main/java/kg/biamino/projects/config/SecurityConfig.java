package kg.biamino.projects.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


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
                         request.requestMatchers("/auth/login", "/auth/logout").permitAll()
                                 .requestMatchers(HttpMethod.POST, "trainees").permitAll()
                                 .requestMatchers(HttpMethod.POST, "trainers").permitAll()
                                 .requestMatchers("trainees").hasAuthority("TRAINEE")
                                 .requestMatchers("trainers").hasAuthority("TRAINER")
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
