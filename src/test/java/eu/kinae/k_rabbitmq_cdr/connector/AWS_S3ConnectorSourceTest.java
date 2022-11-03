package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3ReaderInfo;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AWS_S3ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@Testcontainers
public class AWS_S3ConnectorSourceTest {

    @Container
    public static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14"))
        .withServices(LocalStackContainer.Service.S3);

    private final KParameters parameters = new KParameters(null, null, null, null, null, null, null, Region.of(localstack.getRegion()), null, "prefix", null, null, null);

    private final S3Client s3 = S3Client
        .builder()
        .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
        .region(Region.of(localstack.getRegion()))
        .build();

    @Test
    public void Check_constructor() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var awsS3ReaderInfo = new AWS_S3ReaderInfo(s3, bucket, parameters.prefix(), KOptions.DEFAULT);
        var connector = new AWS_S3ConnectorSource(awsS3ReaderInfo);
        assertThat(connector.getSupportedType()).isEqualTo(SupportedType.AWS_S3);
        assertThat(connector.countMessages(parameters)).isEqualTo(0);
        assertThatNoException().isThrownBy(connector::close);
    }

    @Test
    public void Check_instance_of_direct_linked() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var options = KOptions.DEFAULT;
        var awsS3ReaderInfo = new AWS_S3ReaderInfo(s3, bucket, parameters.prefix(), options);
        var connector = new AWS_S3ConnectorSource(awsS3ReaderInfo);
        var source = connector.getDirectLinked(parameters, options, null);
        assertThat(source).isInstanceOf(AWS_S3Reader.class);
    }

    @Test
    public void Check_instance_of_sequential() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var options = KOptions.DEFAULT;
        var awsS3ReaderInfo = new AWS_S3ReaderInfo(s3, bucket, parameters.prefix(), options);
        var connector = new AWS_S3ConnectorSource(awsS3ReaderInfo);
        var source = connector.getSequentialComponent(null, parameters, options, null);
        assertThat(source).isInstanceOf(SequentialComponentSource.class);
        assertThat(source).matches(it -> it.getSource() instanceof AWS_S3Reader);
    }

    @Test
    public void Check_instance_of_parallels() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var options = KOptions.DEFAULT;
        var awsS3ReaderInfo = new AWS_S3ReaderInfo(s3, bucket, parameters.prefix(), options);
        var connector = new AWS_S3ConnectorSource(awsS3ReaderInfo);
        var source = connector.getParallelComponents(null, parameters, options, null);
        assertThat(source).isInstanceOf(ParallelComponents.class);
        assertThat(source).hasOnlyElementsOfType(ParallelComponentSource.class).hasSize(options.sourceThread());
        assertThat(source).allMatch(it -> ((ParallelComponentSource) it).getSource() instanceof AWS_S3Reader);
    }

}
