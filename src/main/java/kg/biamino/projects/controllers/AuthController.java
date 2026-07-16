package kg.biamino.projects.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.dto.ChangePasswordDto;
import kg.biamino.projects.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthHandler authHandler;
    public AuthController(AuthHandler authHandler, UserService userService) {
        this.authHandler = authHandler;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Void> login(HttpServletRequest httpExchange) {
        authHandler.handle(httpExchange);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest httpExchange) {
        String authUsername = authHandler.handle(httpExchange);
        userService.changePassword(changePasswordDto, authUsername);
        return ResponseEntity.ok().build();
    }




}
