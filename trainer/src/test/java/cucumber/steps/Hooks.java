package cucumber.steps;

import io.cucumber.java.Before;
import kg.biamino.projects.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class Hooks {

    public static String jwtToken;

    @Autowired
    private JwtUtil jwtUtil;

    @Before
    public void generateToken() {
        jwtToken = jwtUtil.generateToken("Akhmatbek.Tursunbaev");
    }
}
