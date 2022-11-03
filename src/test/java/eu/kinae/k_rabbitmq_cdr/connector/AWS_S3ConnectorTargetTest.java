package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Writer;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AWS_S3ConnectorTarget;
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
public class AWS_S3ConnectorTargetTest {

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

        var connector = new AWS_S3ConnectorTarget(s3);
        assertThat(connector.getSupportedType()).isEqualTo(SupportedType.AWS_S3);
        assertThatNoException().isThrownBy(connector::close);
    }

    @Test
    public void Check_instance_of_direct_linked() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var connector = new AWS_S3ConnectorTarget(s3);
        var Target = connector.getDirectLinked(parameters, null);
        assertThat(Target).isInstanceOf(AWS_S3Writer.class);
    }

    @Test
    public void Check_instance_of_sequential() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var options = KOptions.DEFAULT;
        var connector = new AWS_S3ConnectorTarget(s3);
        var target = connector.getSequentialComponent(null, parameters, options, null);
        assertThat(target).isInstanceOf(SequentialComponentTarget.class);
        assertThat(target).matches(it -> it.getTarget() instanceof AWS_S3Writer);
    }

    @Test
    public void Check_instance_of_parallels() {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var options = KOptions.DEFAULT;
        var connector = new AWS_S3ConnectorTarget(s3);
        var target = connector.getParallelComponents(null, parameters, options, null);
        assertThat(target).isInstanceOf(ParallelComponents.class);
        assertThat(target).hasOnlyElementsOfType(ParallelComponentTarget.class).hasSize(options.targetThread());
        assertThat(target).allMatch(it -> ((ParallelComponentTarget) it).getTarget() instanceof AWS_S3Writer);
    }

}
