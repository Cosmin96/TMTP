package com.tmtp.web.TMTP.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    String store(MultipartFile file, String username);

    String storeJobPhoto(MultipartFile file, String id);

    Stream<Path> loadAll();

    Path load(String filename, Path path);

    Path loadJackets(String filename);

    Resource loadAsResource(String filename);

    Resource loadJacketAsResource(String filename);

    void deleteAll();

}
