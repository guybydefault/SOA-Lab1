package ru.guybydefault.web;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/api")
public class JerseyResourceConfig extends ResourceConfig {
    public JerseyResourceConfig() {
        register(FlatJerseyEndpoint.class);
    }
}
