package ru.guybydefault.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import ru.guybydefault.jersey.web.FlatJerseyEndpoint;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class JerseyApplication extends ResourceConfig {
    public JerseyApplication() {
        register(FlatJerseyEndpoint.class); }
}
