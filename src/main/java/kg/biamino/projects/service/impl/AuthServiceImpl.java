package kg.biamino.projects.service.impl;

import kg.biamino.projects.config.BruteForceProtectionService;
import kg.biamino.projects.config.JwtUtil;
import kg.biamino.projects.dto.LoginRequestDto;
import kg.biamino.projects.dto.TokenResponseDto;
import kg.biamino.projects.exception.UserInactiveException;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import kg.biamino.projects.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final BruteForceProtectionService bruteForceProtectionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(JwtUtil jwtUtil, BruteForceProtectionService bruteForceProtectionService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.bruteForceProtectionService = bruteForceProtectionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findUserByUsername(loginRequestDto.username()).orElseThrow(()-> new BadCredentialsException("Username not found."));

        if (bruteForceProtectionService.isBlocked(loginRequestDto.username())) {
            throw new BadCredentialsException("You have been temporarily locked due to too many failed login attempts.");
        }
        if(user.getIsActive().equals(Boolean.FALSE)) {
            throw new UserInactiveException("user is inactive");
        }

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            bruteForceProtectionService.loginFailed(loginRequestDto.username());
            throw new BadCredentialsException("Invalid username or password.");
        }
        bruteForceProtectionService.loginSucceeded(loginRequestDto.username());
        return new TokenResponseDto(jwtUtil.generateToken(loginRequestDto.username()));

    }
}
