package com.tmtp.web.TMTP.service.cloud;

import com.tmtp.web.TMTP.dto.CloudinaryObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudStorageService {

    CloudinaryObject uploadFile(MultipartFile file, String bucketName) throws IOException;
}
