package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.properties.S3Properties;
import freshtrash.freshtrashbackend.exception.FileException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service implements FileService {

    private final S3Client s3;
    private final S3Properties s3Properties;

    /**
     * @reference https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3ObjectOperations.java#L213
     */
    @Override
    public void uploadFile(MultipartFile file, String key) {
        // First create a multipart upload and get the upload id
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(key)
                .build();

        CreateMultipartUploadResponse response = s3.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();

        // Upload all the different parts of the object
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(key)
                .uploadId(uploadId)
                .partNumber(1)
                .build();

        try {
            String etag1 = s3.uploadPart(uploadPartRequest, RequestBody.fromBytes(file.getBytes()))
                    .eTag();
            CompletedPart part =
                    CompletedPart.builder().partNumber(1).eTag(etag1).build();

            // Finally call completeMultipartUpload operation to tell S3 to merge all uploaded
            // parts and finish the multipart operation.
            CompletedMultipartUpload completedMultipartUpload =
                    CompletedMultipartUpload.builder().parts(part).build();

            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(completedMultipartUpload)
                    .build();

            s3.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (IOException e) {
            throw new FileException(ErrorCode.FILE_CANT_SAVE);
        }
    }

    /**
     * Amazon S3에서 파일 삭제 수행
     * @reference https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3Scenario.java#L322
     * @param key 파일명 (확장자 포함)
     */
    @Override
    public void deleteFileIfExists(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .key(key)
                    .build();
            s3.deleteObject(deleteObjectRequest);
        } catch (SdkClientException | S3Exception e) {
            throw new FileException(ErrorCode.FILE_CANT_DELETE);
        }
    }
}
