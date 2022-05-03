package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.nio.file.Path;
import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTest;
import eu.kinae.k_rabbitmq_cdr.protocol.ComponentDirectLinked;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3Writer;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class AMQPComponentDirectLinkedTest extends AbstractComponentTest {

    public final static String PREFIX = "prefix";
    public static final String EMPTY_SOURCE_Q = "empty-source-q";
    public static final String SOURCE_Q = "source-q";
    public static final String TARGET_Q = "target-q";

    @TempDir
    protected Path tempDir;

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(EMPTY_SOURCE_Q)
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
            .credentialsProvider(DefaultCredentialsProvider.create())
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
        s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        try(var source = new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q);
            var target = new AWS_S3Writer(s3, bucket, "prefix")) {

            var component = new ComponentDirectLinked(source, target);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatSourceContainsAllMessagesUnsorted(new AWS_S3Reader(s3, bucket, "prefix", KOptions.DEFAULT));

            // merge S3 reader and writer
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages4() throws Exception {
        for(var message : MESSAGES) {
            new FileWriter(tempDir).push(message);
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
        s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());

        var target2 = new AWS_S3Writer(s3, bucket, PREFIX);
        for(var message : MESSAGES) {
            target2.push(message);
        }

        try(var source = new AWS_S3Reader(s3, bucket, "prefix", KOptions.DEFAULT);
            var target = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatSourceContainsAllMessagesUnsorted(target);
        }
    }
}
