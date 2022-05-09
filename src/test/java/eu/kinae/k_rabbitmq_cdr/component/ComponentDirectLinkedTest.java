package eu.kinae.k_rabbitmq_cdr.component;

import java.nio.file.Path;
import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnection;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Writer;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.component.file.FileWriter;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class ComponentDirectLinkedTest extends AbstractComponentTest {

    public static final String PREFIX = "prefix";
    public static final String SOURCE_Q = "source-q";
    public static final String TARGET_Q = "target-q";

    @TempDir
    protected Path tempDir;

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(SOURCE_Q)
            .withQueue(TARGET_Q);

    @Container
    public static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14"))
            .withServices(LocalStackContainer.Service.S3);

    @BeforeAll
    public static void beforeAll() throws Exception {
        try(var sourceConnection = new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q)) {
            for(var message : MESSAGES) {
                sourceConnection.push(message);
            }
        }
    }

    protected final S3Client s3 = S3Client
            .builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
            .region(Region.of(localstack.getRegion()))
            .build();

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var source = new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q);
            var target = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatSourceContainsAllMessagesSorted(target);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages2() throws Exception {
        try(var source = new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q);
            var target = new FileWriter(tempDir)) {

            var component = new ComponentDirectLinked(source, target);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatSourceContainsAllMessagesUnsorted(new FileReader(tempDir, KOptions.DEFAULT));
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages3() throws Exception {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        try(var source = new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q);
            var target = new AWS_S3Writer(s3, bucket, PREFIX)) {

            var component = new ComponentDirectLinked(source, target);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            var reader = new AWS_S3Reader(s3, bucket, PREFIX, KOptions.DEFAULT);
            assertThatSourceContainsAllMessagesUnsorted(reader);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages4() throws Exception {
        var writer = new FileWriter(tempDir);
        for(var message : MESSAGES) {
            writer.push(message);
        }

        try(var source = new FileReader(tempDir, KOptions.DEFAULT);
            var target = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatSourceContainsAllMessagesSorted(target);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages5() throws Exception {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var writer = new AWS_S3Writer(s3, bucket, PREFIX);
        for(var message : MESSAGES) {
            writer.push(message);
        }

        try(var source = new AWS_S3Reader(s3, bucket, PREFIX, KOptions.DEFAULT);
            var target = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatSourceContainsAllMessagesUnsorted(target);
        }
    }
}
