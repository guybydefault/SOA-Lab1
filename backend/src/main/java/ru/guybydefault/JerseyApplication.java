package ru.guybydefault;

import org.glassfish.jersey.server.ResourceConfig;
import ru.guybydefault.web.CORSFilter;
import ru.guybydefault.web.FlatJerseyEndpoint;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class JerseyApplication extends ResourceConfig {
    public JerseyApplication() {
        register(CORSFilter.class);
        register(FlatJerseyEndpoint.class);
    }
}
