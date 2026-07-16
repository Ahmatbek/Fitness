package kg.biamino.projects.auth;


import jakarta.servlet.http.HttpServletRequest;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AuthHandler implements HandlerInterceptor {

    private final UserService userService;
    public AuthHandler(final UserService userService) {
        this.userService = userService;
    }

    public String handle(HttpServletRequest request)  {
        request.getHeaderNames();
        String auth = request.getHeader("Authorization");

        String trimmed = auth.substring(6);
        byte[] bytes = Base64.getDecoder().decode(trimmed);

        String s = new String(bytes, StandardCharsets.UTF_8);

        String [] credentials = s.split(":");
        String username = credentials[0];
        String password = credentials[1];


        userService.userAuthenticated(username, password);
        return username;
    }

    public static void checkAuthorization(String username, String authUsername){
        if(!username.equals(authUsername)) throw new AuthorizationException("cant modify other users");
    }


}
