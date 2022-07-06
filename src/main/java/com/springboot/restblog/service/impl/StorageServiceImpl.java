package com.springboot.restblog.service.impl;

import com.springboot.restblog.service.IStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

@Service
public class StorageServiceImpl implements IStorageService {

    @Override
    public byte[] readFileContent(Path filePath) {
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                return bytes;
            } else {
                throw new RuntimeException("Could not read file: ");
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: ", e);
        }
    }
}
