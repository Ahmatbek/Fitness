package kg.biamino.projects.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@Service
public class BruteForceProtectionService {

    private static final int MAX_ATTEMPT = 3;
    private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(5);

    private final Map<String, Attempt> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        lockCache.remove(key);
    }

    public void loginFailed(String key) {
        long now = System.currentTimeMillis();
        Attempt updated = attemptsCache.compute(key, (k, current) -> {
            int count = (current == null ? 0 : current.count()) + 1;
            return new Attempt(count, now);
        });
        if (updated.count >= MAX_ATTEMPT) {
            lockCache.put(key, now);
        }
    }

    public boolean isBlocked(String key) {

        if (key==null || !lockCache.containsKey(key)) {
            return false;
        }

        Long lockTime = lockCache.get(key);
        if(lockTime==null){
            return false;
        }
        if (System.currentTimeMillis() - lockTime > LOCK_TIME) {
            lockCache.remove(key);
            attemptsCache.remove(key);
            return false;
        }

        return true;
    }
    @Scheduled(cron = "0 */10 * * * *")
    public void removeUnUsedAttempts() {
        lockCache.entrySet()
                .removeIf(entry -> System.currentTimeMillis() - entry.getValue() > LOCK_TIME);
        attemptsCache.entrySet()
                .removeIf(entry->  System.currentTimeMillis()- entry.getValue().time > LOCK_TIME);
    }

    public record Attempt (int count, long time){}
}
