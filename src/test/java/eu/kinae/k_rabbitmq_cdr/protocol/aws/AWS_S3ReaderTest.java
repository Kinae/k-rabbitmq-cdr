package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import static eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTest.MESSAGES;

@Testcontainers
public class AWS_S3ReaderTest {

    protected final static String PREFIX = "prefix";

    @Container
    public static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14"))
            .withServices(LocalStackContainer.Service.S3);

    protected final S3Client s3 = S3Client
            .builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    @Test
    public void test() throws Exception {
        var option = new KOptions(4);
        s3.createBucket(CreateBucketRequest.builder().bucket("bubu").build());

        var target = new AWS_S3Writer(s3, "bubu", PREFIX);
        for(var message : MESSAGES) {
            target.push(message);
        }

        var reader = new AWS_S3Reader(s3, "bubu", PREFIX, option);
        System.out.println("reader : ");
    }
}
