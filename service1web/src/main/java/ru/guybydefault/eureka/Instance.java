package ru.guybydefault.eureka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonRootName("instance")
public class Instance {

    private static String DEFAULT_PORT = "10241";

    private String instanceId = "mainInstance";
    private String hostName = "localhost";
    private String ipAddr = "localhost";
    private String vipAddress = "main";
    private String app = "main";
    private String status = "UP";
    private Port port = new Port(System.getenv().getOrDefault("MAIN_WEB_PORT", DEFAULT_PORT), false);
    private Port securePort = new Port(System.getenv().getOrDefault("MAIN_WEB_PORT", DEFAULT_PORT), true);
    private DataCenterInfo dataCenterInfo = new DataCenterInfo();

    @Data
    @JsonRootName("dataCenterInfo")
    public static class DataCenterInfo {
        @JsonProperty("@class")
        private final String cl = "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo";
        private String name = "MyOwn";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Port {
        @JsonProperty("$")
        private String port;
        @JsonProperty("@enabled")
        private boolean enabled = true;
    }
}
