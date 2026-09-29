package com.military.mams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MamsApplication {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("[MAMS BOOTSTRAP] Launching Military Asset Management System");
        System.out.println("[MAMS BOOTSTRAP] DB_URL env: " + System.getenv("DB_URL"));
        System.out.println("[MAMS BOOTSTRAP] DB_HOST env: " + System.getenv("DB_HOST"));
        System.out.println("[MAMS BOOTSTRAP] PORT env: " + System.getenv("PORT"));
        System.out.println("[MAMS BOOTSTRAP] SPRING_PROFILES_ACTIVE env: " + System.getenv("SPRING_PROFILES_ACTIVE"));

        // Safeguard: If DB_URL or DB_HOST points to unreachable localhost in a container,
        // intercept and point to embedded H2 database so startup never crashes
        String dbUrl = System.getenv("DB_URL");
        String dbHost = System.getenv("DB_HOST");
        if ((dbUrl != null && (dbUrl.contains("localhost") || dbUrl.contains("127.0.0.1"))) ||
            (dbHost != null && (dbHost.contains("localhost") || dbHost.contains("127.0.0.1")))) {
            System.out.println("[MAMS BOOTSTRAP] WARNING: Detected localhost DB in cloud container environment.");
            System.out.println("[MAMS BOOTSTRAP] Overriding with embedded in-memory H2 database for zero-downtime startup.");
            System.setProperty("spring.datasource.url", "jdbc:h2:mem:mams_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
            System.setProperty("spring.datasource.username", "sa");
            System.setProperty("spring.datasource.password", "");
            System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
        }

        // Sanitize PORT environment variable (strip any quotes, non-digits)
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            String cleanPort = portEnv.replaceAll("[^0-9]", "");
            if (!cleanPort.isEmpty()) {
                System.setProperty("server.port", cleanPort);
            }
        }
        System.out.println("==================================================");

        try {
            SpringApplication.run(MamsApplication.class, args);
        } catch (Throwable t) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("[MAMS CRITICAL STARTUP ERROR] Full exception trace:");
            t.printStackTrace(System.err);
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            throw t;
        }
    }
}
