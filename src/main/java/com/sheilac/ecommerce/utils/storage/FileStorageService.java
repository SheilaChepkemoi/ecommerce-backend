package com.sheilac.ecommerce.utils.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /** Stores the file under the directory and returns the RELATIVE path. */
    String store(MultipartFile file, String directory, String extension);

    void delete(String storagePath);

    Resource load(String storagePath);
}
