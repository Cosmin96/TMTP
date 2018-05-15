package com.tmtp.web.TMTP.service.cloud;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmtp.web.TMTP.constants.CommonKey;
import com.tmtp.web.TMTP.dto.CloudinaryObject;
import com.tmtp.web.TMTP.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * This is a service which only concerns itself to upload file on the Cloudinary.
 *
 * Note that this service doesn't need to know who is the caller and what type
 * of file it wants to upload to the cloud. Also, caller should itself manage if any
 * files are needed to be deleted when new files are uploaded (like deleting older profile
 * image when new one is uploaded).
 */
@Service
public class CloudStorageServiceImpl implements CloudStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(CloudStorageServiceImpl.class);

    private final Cloudinary cloudinary;
    private final ObjectMapper objectMapper;

    @Autowired
    public CloudStorageServiceImpl(Cloudinary cloudinary, ObjectMapper objectMapper) {
        this.cloudinary = cloudinary;
        this.objectMapper = objectMapper;
    }

    /**
     * Find logic for Cloudinary file upload
     * here: https://cloudinary.com/documentation/java_image_upload#server_side_upload
     *
     * @param file File to be uploaded
     * @param bucketName Folder where this file needs to be uploaded
     * @return Response received from Cloudinary
     */
    @Override
    public CloudinaryObject uploadFile(MultipartFile file, String bucketName) {

        String profileImgIdentifier = UUID.randomUUID().toString().replace("-", "");

        Map params = ObjectUtils.asMap(
                //Provide the Cloudinary folder name where this image should be uploaded
                CommonKey.PUBLIC_ID, bucketName + File.separator + profileImgIdentifier + file.getName(),

                //Ensure that this file has a unique name when it is saved to Cloudinary
                CommonKey.UNIQUE_FILENAME, true);
        LOG.info("Uploading file to cloudinary bucket {}.", bucketName);

        CloudinaryObject cloudinaryObject = null;
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            cloudinaryObject = objectMapper.convertValue(uploadResult, CloudinaryObject.class);
            LOG.info("File uploaded to cloudinary: {}", cloudinaryObject);
        } catch (IOException | IllegalArgumentException ex) {
            LOG.error("Failed to Upload file to Cloudinary. Exception is: [{}]", ex);
        }

        //Return cloudinaryObject so that the caller can decide what to do with all the information
        return cloudinaryObject;
    }
}
