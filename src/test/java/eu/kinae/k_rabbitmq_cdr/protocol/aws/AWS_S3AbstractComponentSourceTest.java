package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSourceTest;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import static org.mockito.Mockito.mock;

@Testcontainers
public abstract class AWS_S3AbstractComponentSourceTest extends AbstractComponentSourceTest {

    protected final static String PREFIX = "prefix";

    @Container
    public static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14"))
            .withServices(LocalStackContainer.Service.S3);

    protected final S3Client s3 = S3Client
            .builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
            .region(Region.of(localstack.getRegion()))
            .build();

    @Override
    protected AWS_S3Reader getEmptySource() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());

        return new AWS_S3Reader(s3, bucket, PREFIX, KOptions.DEFAULT);
    }

    @Override
    protected AWS_S3Reader getSource() throws Exception {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());

        var target = new AWS_S3Writer(s3, bucket, PREFIX);
        for(var message : MESSAGES) {
            target.push(message);
        }

        return new AWS_S3Reader(s3, bucket, PREFIX, KOptions.DEFAULT);
    }

    protected SharedQueue getMockTarget() {
        return mock(SharedQueue.class);
    }

    protected abstract SharedQueue getSharedQueue();

}