package kg.biamino.projects.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Setter

public class AppUserDetails extends User {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


    public AppUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
