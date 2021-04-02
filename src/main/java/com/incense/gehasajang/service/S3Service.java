package com.incense.gehasajang.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.incense.gehasajang.error.ErrorCode;
import com.incense.gehasajang.exception.CannotConvertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile image, String dirName) throws IOException {
        if (image == null || image.isEmpty()) {
            return "";
        }

        File uploadFile = convert(image)
                .orElseThrow(() -> new CannotConvertException(ErrorCode.CANNOT_CONVERT_FILE));

        return upload(uploadFile, dirName);
    }

    private String upload(File image, String dirName) {
        StringBuffer fileName = new StringBuffer(dirName);
        fileName.append("/");
        fileName.append(image.getName());
        fileName.append("-");
        fileName.append(UUID.randomUUID().toString().replace("-", ""));
        fileName.append("-");
        fileName.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        String uploadImageUrl = putS3(image, fileName.toString());
        removeNewFile(image);
        return uploadImageUrl;
    }

    private String putS3(File image, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, image).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 않았습니다.");
        }
    }

    private Optional<File> convert(MultipartFile image) throws IOException {
        File convertFile = new File("./image", image.getOriginalFilename());
        log.info("convertFile path: {}", convertFile.getAbsolutePath());

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(image.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }


}
