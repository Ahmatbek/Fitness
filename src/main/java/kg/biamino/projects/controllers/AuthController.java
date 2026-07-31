package kg.biamino.projects.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.biamino.projects.dto.ChangePasswordDto;
import kg.biamino.projects.dto.LoginRequestDto;
import kg.biamino.projects.dto.TokenResponseDto;
import kg.biamino.projects.service.AuthService;
import kg.biamino.projects.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication and credential management")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    public AuthController( UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "login with username and password (Basic Auth header)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "credentials matched"),
            @ApiResponse(responseCode = "401", description = "username and password do not match")
    })
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PutMapping
    @Operation(summary = "change login password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "password changed"),
            @ApiResponse(responseCode = "400", description = "required fields missing or new password doesnt meet complexity rules"),
            @ApiResponse(responseCode = "401", description = "old password doesnt match"),
            @ApiResponse(responseCode = "403", description = "not allowed to change another user's password")
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, Principal principal) {
        userService.changePassword(changePasswordDto, principal.getName());
        return ResponseEntity.ok().build();
    }




}
