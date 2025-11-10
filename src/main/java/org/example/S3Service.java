package org.example;

import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

    private final AmazonS3 s3;
    private final FileService fileService;
    private final String bucketName;


    @Autowired
    public S3Service(AmazonS3 s3, FileService fileService, @Value("${aws.mentoring.bucket-name}") String bucketName) {
        this.s3 = s3;
        this.fileService = fileService;
        this.bucketName = bucketName;
    }

    public byte[] downloadObject(String objectName) {
        checkIfBucketExists();
        checkIfObjectExits(objectName);

        S3Object s3Object = s3.getObject(bucketName, objectName);
        return fileService.readBitmap(s3Object);
    }

    public void uploadObject(InputStream file, String filename, String customName) {
        checkIfBucketExists();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("Name", filename);
        metadata.setContentType("image/jpg");
        PutObjectRequest request = new PutObjectRequest(bucketName, customName, file, metadata);
        request.setMetadata(metadata);
        s3.putObject(request);
    }

    public void deleteObject(String objectName) {
        checkIfBucketExists();
        checkIfObjectExits(objectName);
        s3.deleteObject(bucketName, objectName);
    }

    private void checkIfBucketExists() {
        if (!s3.doesBucketExistV2(bucketName)) {
            s3.createBucket(bucketName);
        }
    }

    private void checkIfObjectExits(String objectName) {
        if (!s3.doesObjectExist(bucketName, objectName)) {
            throw new RuntimeException();
        }
    }
}
