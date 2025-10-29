package com.manager.ads.Service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import java.util.UUID;

@Service
public class S3Service {

    private final String bucketName;
    private final String cloudfrontUrl;
    private final S3Client s3Client;

    public S3Service() {
        Dotenv dotenv = Dotenv.load();

        String accessKey = dotenv.get("S3_ACCESS_KEY");
        String secretKey = dotenv.get("S3_SECRET_ACCESS_KEY");
        String region = dotenv.get("AWS_S3_REGION");

        this.bucketName = dotenv.get("BUCKET_NAME");
        this.cloudfrontUrl = dotenv.get("CLOUDFRONT");

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String key = "campaigns/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putOb, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        return cloudfrontUrl + "/" + key;
    }
}
 