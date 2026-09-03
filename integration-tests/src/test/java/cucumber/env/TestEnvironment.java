package cucumber.env;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class TestEnvironment {

    private static final String SECRET_KEY =
            "dGVzdC1zZWNyZXQta2V5LWZvci1qd3Qtc2lnbmluZy1tdXN0LWJlLWxvbmctZW5vdWdoLWZvci1obWFjLTMy";
    private static final String ACTIVEMQ_USER = "admin";
    private static final String ACTIVEMQ_PASSWORD = "admin";

    public static int fitnessPort;
    public static int trainerPort;

    private static PostgreSQLContainer<?> postgres;
    private static MongoDBContainer mongo;
    private static GenericContainer<?> activemq;
    private static Process fitnessProcess;
    private static Process trainerProcess;

    private static boolean started = false;

    private TestEnvironment() {
    }

    public static synchronized void startOnce() {
        if (started) {
            return;
        }
        started = true;

        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("fitness")
                .withUsername("fitness")
                .withPassword("fitness");
        postgres.start();

        mongo = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));
        mongo.start();

        activemq = new GenericContainer<>(DockerImageName.parse("rmohr/activemq:latest"))
                .withExposedPorts(61616)
                .waitingFor(Wait.forListeningPort())
                .withStartupTimeout(Duration.ofMinutes(2));
        activemq.start();

        String activemqUrl = "tcp://" + activemq.getHost() + ":" + activemq.getMappedPort(61616);

        fitnessPort = findFreePort();
        trainerPort = findFreePort();

        fitnessProcess = launch("fitness", fitnessPort, List.of(
                "-Dspring.datasource.url=jdbc:postgresql://" + postgres.getHost() + ":" + postgres.getMappedPort(5432) + "/" + postgres.getDatabaseName(),
                "-Dspring.datasource.username=" + postgres.getUsername(),
                "-Dspring.datasource.password=" + postgres.getPassword(),
                "-Dspring.activemq.broker-url=" + activemqUrl,
                "-Dspring.activemq.user=" + ACTIVEMQ_USER,
                "-Dspring.activemq.password=" + ACTIVEMQ_PASSWORD,
                "-Dspring.security.jwt.secret-key=" + SECRET_KEY,
                "-Dapplication.domain=localhost",
                "-Deureka.client.enabled=false"
        ));
        awaitHealthy("http://localhost:" + fitnessPort + "/actuator/health", 90);

        trainerProcess = launch("trainer", trainerPort, List.of(
                "-Dspring.data.mongodb.uri=" + mongo.getReplicaSetUrl(),
                "-Dspring.activemq.broker-url=" + activemqUrl,
                "-Dspring.activemq.user=" + ACTIVEMQ_USER,
                "-Dspring.activemq.password=" + ACTIVEMQ_PASSWORD,
                "-Dspring.security.jwt.secret-key=" + SECRET_KEY,
                "-Deureka.client.enabled=false"
        ));
        awaitReachable("http://localhost:" + trainerPort + "/trainers/readiness-probe/summary", 90);

        Runtime.getRuntime().addShutdownHook(new Thread(TestEnvironment::stopAll));
    }

    public static synchronized void stopAll() {
        if (fitnessProcess != null) {
            fitnessProcess.destroyForcibly();
        }
        if (trainerProcess != null) {
            trainerProcess.destroyForcibly();
        }
        if (activemq != null) {
            activemq.stop();
        }
        if (mongo != null) {
            mongo.stop();
        }
        if (postgres != null) {
            postgres.stop();
        }
    }

    private static Process launch(String moduleName, int port, List<String> extraJvmArgs) {
        File jar = findJar(moduleName);
        try {
            List<String> command = new ArrayList<>();
            command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
            command.add("-Dserver.port=" + port);
            command.addAll(extraJvmArgs);
            command.add("-jar");
            command.add(jar.getAbsolutePath());

            File logFile = new File("target/" + moduleName + "-integration.log");
            logFile.getParentFile().mkdirs();

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectOutput(ProcessBuilder.Redirect.to(logFile));
            builder.redirectErrorStream(true);
            return builder.start();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to launch " + moduleName + " from " + jar, e);
        }
    }

    private static File findJar(String moduleName) {
        File targetDir = new File("../" + moduleName + "/target");
        File[] jars = targetDir.listFiles((dir, name) ->
                name.startsWith(moduleName + "-") && name.endsWith(".jar")
                        && !name.endsWith("-sources.jar") && !name.endsWith("-tests.jar"));
        if (jars == null || jars.length == 0) {
            throw new IllegalStateException("No executable jar found for '" + moduleName + "' in "
                    + targetDir.getAbsolutePath()
                    + " - the integration suite needs the real jars built first. Run `mvn -Pintegration verify` "
                    + "(or `package`) from the repo root, not `mvn test`.");
        }
        return jars[0];
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Could not find a free port", e);
        }
    }

    private static void awaitHealthy(String url, int timeoutSeconds) {
        HttpClient client = HttpClient.newHttpClient();
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        Exception lastError = null;
        while (System.currentTimeMillis() < deadline) {
            try {
                HttpResponse<String> response = client.send(
                        HttpRequest.newBuilder(URI.create(url)).GET().build(),
                        HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() < 500) {
                    return;
                }
            } catch (Exception e) {
                lastError = e;
            }
            sleep(500);
        }
        throw new IllegalStateException("Service at " + url + " did not become healthy in time", lastError);
    }

    private static void awaitReachable(String url, int timeoutSeconds) {
        HttpClient client = HttpClient.newHttpClient();
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        Exception lastError = null;
        while (System.currentTimeMillis() < deadline) {
            try {
                client.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                        HttpResponse.BodyHandlers.discarding());
                return;
            } catch (Exception e) {
                lastError = e;
            }
            sleep(500);
        }
        throw new IllegalStateException("Service at " + url + " never accepted a request in time", lastError);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
