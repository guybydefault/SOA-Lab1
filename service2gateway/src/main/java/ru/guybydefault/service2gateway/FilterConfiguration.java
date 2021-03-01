package ru.guybydefault.service2gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Bean
    public PostFilter getPostFilter() {
        return new PostFilter();
    }

    @Bean
    public PreFilter getPreFilter() {
        return new PreFilter();
    }

    @Bean
    public RouteFilter getRouteFilter() {
        return new RouteFilter();
    }
}
