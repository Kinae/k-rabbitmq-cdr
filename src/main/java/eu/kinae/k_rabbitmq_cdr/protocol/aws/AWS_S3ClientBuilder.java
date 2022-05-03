package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ClientBuilder {

    public static S3Client build() {
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.EU_WEST_1) // parameters
                .build();
    }

}
