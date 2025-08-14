// src/main/java/com/example/demo/config/MvcConfig.java
package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir.avatars}")
    private String avatarUploadDir;

    @Value("${file.upload-dir.violations}")
    private String violationUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("user-avatars", avatarUploadDir, registry);
        exposeDirectory("violation-images", violationUploadDir, registry);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/contact/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
    private void exposeDirectory(String urlPath, String physicalPath, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(physicalPath);
        String absolutePath = uploadDir.toFile().getAbsolutePath();

        String location = uploadDir.toUri().toString();

        registry.addResourceHandler("/" + urlPath + "/**")
                .addResourceLocations(location);

    }
}