package com.springboot.restblog.controller;

import com.springboot.restblog.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = "*", maxAge = 31536000)
@RequestMapping("/files")
public class FileUploadController {

    @Autowired
    private IStorageService storageService;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    //get image url
    @GetMapping("/**")
    public ResponseEntity<byte[]> readDetailFile(HttpServletRequest request) {

        String uri = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String path = antPathMatcher.extractPathWithinPattern(pattern, uri);

        Path toPath = Paths.get(path);
        try {
            byte[] bytes = storageService.readFileContent(toPath);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(bytes);
        } catch (Exception exception) {
            return ResponseEntity.noContent().build();
        }
    }
}
