package eu.kinae.k_rabbitmq_cdr.connector;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.params.TransferType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.regions.Region;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class ConnectorFactoryTest {

    @TempDir
    protected Path tempDir;

    @Container
    public static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14"))
        .withServices(LocalStackContainer.Service.S3);

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"));

    @Test
    public void Check_to_instantiate_all_connectors() throws Exception {
        //TODO
        for(var entrySource : ConnectorFactory.connectorSources.entrySet()) {
            for(var entryTarget : ConnectorFactory.connectorTargets.entrySet()) {
                try(Connector connector = ConnectorFactory.newConnector(parameters(entrySource.getKey(), entryTarget.getKey()))) {
                    assertThat(connector).isNotNull();
                }
            }
        }
    }

    private KParameters parameters(SupportedType sType, SupportedType tType) {
        return new KParameters(sType, AMQPUtils.buildAMQPURI(rabbitmq), "",
                               tType, AMQPUtils.buildAMQPURI(rabbitmq), "",
                               tempDir.toString(), Region.of(localstack.getRegion()), "", "", "", TransferType.DIRECT, ProcessType.SEQUENTIAL);
    }

}
