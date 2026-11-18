package com.sheilac.ecommerce.utils.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.file.storage")
public record FileStorageProperties(String location) {}
