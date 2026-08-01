package kg.biamino.projects.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtLogoutTokens {
    private final Map<String , Instant> invalid = new ConcurrentHashMap<>();

    public void invalidate(String jti, Instant token){
        invalid.put(jti, token);
    }

    public boolean isInvalid(String jti){
        return invalid.containsKey(jti);
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void cleanUp(){
       invalid.values().removeIf(instant -> instant.isBefore(Instant.now()));
    }
}
