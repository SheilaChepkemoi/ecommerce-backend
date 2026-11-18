package com.sheilac.ecommerce.utils.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
@RequiredArgsConstructor
public class FileStorageConfig implements WebMvcConfigurer {

    private final FileStorageProperties props;

    /** Serves GET /images/** straight from the upload folder. */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(props.location()).toAbsolutePath().normalize();

        // toUri() encodes the space in "MY PROJECTS"
        String location = root.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler("/images/**")
                .addResourceLocations(location)
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());
    }
}
