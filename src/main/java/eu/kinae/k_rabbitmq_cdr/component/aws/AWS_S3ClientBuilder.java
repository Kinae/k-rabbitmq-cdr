package eu.kinae.k_rabbitmq_cdr.component.aws;

import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ClientBuilder {

    public static S3Client build(KParameters parameters) {
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(parameters.region())
                .build();
    }

}
