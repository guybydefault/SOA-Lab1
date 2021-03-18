package ru.guybydefault.eureka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class Eureka implements ServletContextListener {
    String eurekaHost = System.getenv().getOrDefault("EUREKA_SERVER", "http://127.0.0.1:10251");
    private Logger logger = Logger.getLogger(getClass().getName());
    private Instance instance = new Instance();
    private ObjectMapper objectMapper = new ObjectMapper();
    private Client client = ClientBuilder.newClient();
    private ScheduledExecutorService scheduledExecutorService;

    public Eureka() {
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        logger.info("Eureka class has been created");
    }


    private void register() {
        logger.info("Registering in Eureka started");
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                Logger.getLogger("Making post request to register the service in eureka");
                Response response = client.target(eurekaHost)
                        .path("eureka/apps/" + instance.getApp())
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.json(objectMapper.writeValueAsString(instance)));

                if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                    logger.info("Service has been successfully registered in eureka (204 code received)");
                } else {
                    logger.warning("Can't connect to the eureka server due to illegal status code from eureka (" + response.getStatus() + ").");
                    logger.warning("Retrying registering the service in eureka");
                }
            } catch (JsonProcessingException | ProcessingException e) {
                logger.log(Level.SEVERE, "Error registering in eureka: " + e.getMessage());
//                        e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void unregister() {
        Response response = client.target(eurekaHost)
                .path("eureka/apps/" + instance.getApp() + "/" + instance.getInstanceId())
                .request()
                .delete();
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.warning("Failed to unregister service in eureka");
        } else {
            logger.info("Successfully unregistered service in eureka");
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        register();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduledExecutorService.shutdownNow();
        unregister();

    }
}
