package ru.guybydefault.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**")
//                .addResourceLocations("classpath:/resources/static/")
//                .resourceChain(true)
//                .addResolver(new PathResourceResolver() {
//                    @Override
//                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
//                        Resource requestedResource = location.createRelative(resourcePath);
//
//                        return requestedResource.exists() && requestedResource.isReadable()
//                                ? requestedResource
//                                : new ClassPathResource("/resources/static/index.html");
//                    }
//                });
//    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/")
//                .setViewName("forward:/index.html");
//        registry.addViewController("")
//                .setViewName("forward:/index.html");
//    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedMethods("*")
//                .allowedOrigins("http://localhost:4200")
//                .allowedMethods("POST, GET, OPTIONS, DELETE, PUT")
//                .allowedHeaders("Origin, X-Requested-With, Content-Type, Accept")
//                .maxAge(3600);
//    }
}
