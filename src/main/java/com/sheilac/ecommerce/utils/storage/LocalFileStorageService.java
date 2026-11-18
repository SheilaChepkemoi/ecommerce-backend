package com.sheilac.ecommerce.utils.storage;
import com.sheilac.ecommerce.utils.exceptions.FileStorageException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final FileStorageProperties properties;

    private Path rootLocation;

    @PostConstruct
    public void initialize() {

        try {

            if (properties.location() == null
                    || properties.location().isBlank()) {

                throw new FileStorageException(
                        "File storage location has not been configured."
                );
            }

            rootLocation = Path.of(properties.location())
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(rootLocation);

        } catch (IOException exception) {

            throw new FileStorageException(
                    "Could not initialize file storage location.",
                    exception
            );
        }
    }

    @Override
    public String store(
            MultipartFile file,
            String directory,
            String extension
    ) {

        try {

            Path targetDirectory = resolveSafely(directory);

            Files.createDirectories(targetDirectory);

            String fileName = UUID.randomUUID() + "." + extension;

            Path destination = resolveSafely(
                    directory + "/" + fileName
            );

            try (InputStream inputStream = file.getInputStream()) {

                Files.copy(
                        inputStream,
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            // Store a relative path in the database.
            return directory + "/" + fileName;

        } catch (IOException exception) {

            throw new FileStorageException(
                    "Could not store the uploaded image.",
                    exception
            );
        }
    }

    @Override
    public void delete(String storagePath) {

        try {

            Path file = resolveSafely(storagePath);

            Files.deleteIfExists(file);

        } catch (IOException exception) {

            throw new FileStorageException(
                    "Could not delete the stored image.",
                    exception
            );
        }
    }

    @Override
    public Resource load(String storagePath) {

        try {

            Path file = resolveSafely(storagePath);

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new FileStorageException(
                    "Image file does not exist or cannot be read."
            );

        } catch (IOException exception) {

            throw new FileStorageException(
                    "Could not load the stored image.",
                    exception
            );
        }
    }

    private Path resolveSafely(String relativePath) {

        if (relativePath == null || relativePath.isBlank()) {
            throw new FileStorageException(
                    "Invalid storage path."
            );
        }

        if (rootLocation == null) {
            throw new FileStorageException(
                    "File storage has not been initialized."
            );
        }

        // Normalize separators before resolving the path.
        String normalizedPath = relativePath.replace('\\', '/');

        Path resolvedPath = rootLocation
                .resolve(normalizedPath)
                .normalize();

        if (!resolvedPath.startsWith(rootLocation)
                || resolvedPath.equals(rootLocation)) {

            throw new FileStorageException(
                    "Invalid storage path."
            );
        }

        return resolvedPath;
    }
}