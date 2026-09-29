package com.military.mams.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

@Component
public class PortCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        int targetPort = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            String digits = portEnv.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                try {
                    int p = Integer.parseInt(digits);
                    if (p > 0 && p <= 65535) {
                        targetPort = p;
                    }
                } catch (NumberFormatException ignored) {
                    // Fall back to default 8080
                }
            }
        }
        factory.setPort(targetPort);
        System.out.println("[MAMS WEB SERVER] Successfully bound web server to port " + targetPort);
    }
}
