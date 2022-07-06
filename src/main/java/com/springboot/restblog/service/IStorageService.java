package com.springboot.restblog.service;

import java.nio.file.Path;

public interface IStorageService {
    byte[] readFileContent(Path path);
}
