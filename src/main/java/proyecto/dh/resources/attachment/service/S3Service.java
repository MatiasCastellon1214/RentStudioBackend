package proyecto.dh.resources.attachment.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.exceptions.handler.BadRequestException;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final SecureRandom secureRandom = new SecureRandom();

    public S3Service(@Value("${aws.accessKeyId}") String accessKeyId,
                     @Value("${aws.secretAccessKey}") String secretAccessKey,
                     @Value("${aws.s3.region}") String region,
                     @Value("${aws.s3.bucketName}") String bucketName) {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        this.bucketName = bucketName;
    }

    private String generateRandomFileName() {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    public String uploadFile(MultipartFile file) throws IOException, BadRequestException {
        if (file.isEmpty()) {
            throw new BadRequestException("El archivo no puede estar vacío");
        }

        String fileExtension = "";
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String key = generateRandomFileName() + fileExtension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(putObjectRequest);

        logger.info("File uploaded to S3 with key: {}", key);
        return key; // Devolver el nombre del archivo generado
    }

    public String getFileUrl(String key) {
        return s3Client.getUrl(bucketName, key).toString();
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
            logger.info("File deleted from S3 with key: {}", key);

            // Verificar si el archivo todavía existe
            boolean exists = s3Client.doesObjectExist(bucketName, key);
            if (exists) {
                logger.error("Failed to delete file from S3 with key: {}", key);
            } else {
                logger.info("File successfully deleted from S3 with key: {}", key);
            }
        } catch (Exception e) {
            logger.error("Error deleting file from S3 with key: {}", key, e);
            throw e;
        }
    }
}