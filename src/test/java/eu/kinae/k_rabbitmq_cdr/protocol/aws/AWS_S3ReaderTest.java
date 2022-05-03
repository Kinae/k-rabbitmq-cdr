package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Testcontainers
public class AWS_S3ReaderTest extends AbstractComponentTest {

    protected final static String PREFIX = "prefix";

    @Container
    public static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14"))
            .withServices(LocalStackContainer.Service.S3);

    protected final S3Client s3 = S3Client
            .builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.of(localstack.getRegion()))
            .build();

    @Test
    public void Read_messages_are_equal_to_original() throws Exception {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        var target = new AWS_S3Writer(s3, bucket, PREFIX);
        for(var message : MESSAGES) {
            target.push(message);
        }

        var reader = new AWS_S3Reader(s3, bucket, PREFIX, KOptions.DEFAULT);

        assertThatSourceContainsAllMessagesUnsorted(reader);
    }
}
