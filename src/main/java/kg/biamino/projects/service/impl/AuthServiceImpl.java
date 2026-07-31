package kg.biamino.projects.service.impl;

import kg.biamino.projects.config.JwtUtil;
import kg.biamino.projects.dto.LoginRequestDto;
import kg.biamino.projects.dto.TokenResponseDto;
import kg.biamino.projects.service.AuthService;
import kg.biamino.projects.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthServiceImpl(UserService userService, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        userService.userAuthenticated(loginRequestDto.username(), loginRequestDto.password());
       return new TokenResponseDto(jwtUtil.generateToken(loginRequestDto.username()));

    }
}
