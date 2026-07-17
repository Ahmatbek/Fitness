package kg.biamino.projects.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.dto.ChangePasswordDto;
import kg.biamino.projects.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication and credential management")
public class AuthController {

    private final UserService userService;
    private final AuthHandler authHandler;
    public AuthController(AuthHandler authHandler, UserService userService) {
        this.authHandler = authHandler;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "login with username and password (Basic Auth header)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "credentials matched"),
            @ApiResponse(responseCode = "401", description = "username and password do not match")
    })
    public ResponseEntity<Void> login(HttpServletRequest httpExchange) {
        authHandler.handle(httpExchange);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "change login password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "password changed"),
            @ApiResponse(responseCode = "400", description = "required fields missing or new password doesnt meet complexity rules"),
            @ApiResponse(responseCode = "401", description = "old password doesnt match"),
            @ApiResponse(responseCode = "403", description = "not allowed to change another user's password")
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest httpExchange) {
        String authUsername = authHandler.handle(httpExchange);
        userService.changePassword(changePasswordDto, authUsername);
        return ResponseEntity.ok().build();
    }




}
