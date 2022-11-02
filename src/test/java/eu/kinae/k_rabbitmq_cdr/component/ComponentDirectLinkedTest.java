package eu.kinae.k_rabbitmq_cdr.component;

import java.nio.file.Path;
import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnectionReader;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnectionWriter;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3ReaderInfo;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Writer;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReaderInfo;
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

import static eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils.buildAMQPConnection;
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
        try(var target = new AMQPConnectionWriter(buildAMQPConnection(rabbitmq), SOURCE_Q)) {
            for(var message : MESSAGES) {
                target.push(message);
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
    public void Produced_messages_are_equal_to_consumed_messages_AMQP_to_AMQP() throws Exception {
        var options = KOptions.SORTED;
        try(var source = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), SOURCE_Q);
            var target = new AMQPConnectionWriter(buildAMQPConnection(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target, options);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            try(var targetAsSource = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), TARGET_Q)) {
                assertThatContainsAllMessages(targetAsSource, options);
            }
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages_AMQP_to_AMQP_body_only() throws Exception {
        var options = KOptions.BODY_ONLY;
        try(var source = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), SOURCE_Q);
            var target = new AMQPConnectionWriter(buildAMQPConnection(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target, options);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            try(var targetAsSource = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), TARGET_Q)) {
                assertThatContainsAllMessages(targetAsSource, options);
            }
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages_AMQP_to_File() throws Exception {
        var options = KOptions.DEFAULT;
        try(var source = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), SOURCE_Q);
            var target = new FileWriter(tempDir)) {

            var component = new ComponentDirectLinked(source, target, options);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            var fileReaderInfo = new FileReaderInfo(tempDir, options);
            assertThatContainsAllMessages(new FileReader(fileReaderInfo), options);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages_AMQP_to_AWS_S3() throws Exception {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var options = KOptions.DEFAULT;
        try(var source = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), SOURCE_Q);
            var target = new AWS_S3Writer(s3, bucket, PREFIX)) {

            var component = new ComponentDirectLinked(source, target, options);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            var awsS3ReaderInfo = new AWS_S3ReaderInfo(s3, bucket, PREFIX, options);
            assertThatContainsAllMessages(new AWS_S3Reader(awsS3ReaderInfo), options);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages_File_to_AMQP() throws Exception {
        var options = KOptions.SORTED;
        var writer = new FileWriter(tempDir);
        for(var message : MESSAGES) {
            writer.push(message);
        }

        var fileReaderInfo = new FileReaderInfo(tempDir, options);
        try(var source = new FileReader(fileReaderInfo);
            var target = new AMQPConnectionWriter(buildAMQPConnection(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target, options);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            try(var targetAsSource = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), TARGET_Q)) {
                assertThatContainsAllMessages(targetAsSource, options);
            }
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages_AWS_S3_to_AMQP() throws Exception {
        var options = KOptions.SORTED;
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var writer = new AWS_S3Writer(s3, bucket, PREFIX);
        for(var message : MESSAGES) {
            writer.push(message);
        }

        var awsS3ReaderInfo = new AWS_S3ReaderInfo(s3, bucket, PREFIX, options);
        try(var source = new AWS_S3Reader(awsS3ReaderInfo);
            var target = new AMQPConnectionWriter(buildAMQPConnection(rabbitmq), TARGET_Q)) {

            var component = new ComponentDirectLinked(source, target, options);

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            try(var targetAsSource = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), TARGET_Q)) {
                assertThatContainsAllMessages(targetAsSource, options);
            }
        }
    }

}
