package eu.kinae.k_rabbitmq_cdr.component.aws;

import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ClientBuilder {

    public static S3Client build(KParameters parameters) {
        if(parameters.profile() != null) {
            return s3Client(ProfileCredentialsProvider.create(parameters.profile()), parameters.region());
        } else {
            return s3Client(DefaultCredentialsProvider.create(), parameters.region());
        }
    }

    private static S3Client s3Client(AwsCredentialsProvider provider, Region region) {
        return S3Client.builder()
                .credentialsProvider(provider)
                .region(region)
                .build();
    }

}
