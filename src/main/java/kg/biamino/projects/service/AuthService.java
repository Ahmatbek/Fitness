package kg.biamino.projects.service;

import kg.biamino.projects.dto.LoginRequestDto;
import kg.biamino.projects.dto.TokenResponseDto;

public interface AuthService {
    TokenResponseDto login(LoginRequestDto loginRequestDto);
}
