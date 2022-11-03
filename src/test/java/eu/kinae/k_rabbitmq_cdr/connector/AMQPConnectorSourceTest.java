package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnectionReader;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AMQPConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@Testcontainers
public class AMQPConnectorSourceTest {

    private static final String SOURCE_Q = "source-q";

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
        .withQueue(SOURCE_Q);

    private final KParameters parameters = new KParameters(SupportedType.AMQP, AMQPUtils.buildAMQPURI(rabbitmq), SOURCE_Q, null, null, null, null, null, null, null, null, null, null);

    @Test
    public void Check_constructor() {
        var connector = new AMQPConnectorSource(parameters, KOptions.DEFAULT);
        assertThat(connector.getSupportedType()).isEqualTo(SupportedType.AMQP);
        assertThat(connector.countMessages(parameters)).isEqualTo(0);
        assertThatNoException().isThrownBy(connector::close);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> connector.countMessages(parameters));
    }

    @Test
    public void Check_instance_of_direct_linked() {
        var options = KOptions.DEFAULT;
        var connector = new AMQPConnectorSource(parameters, options);
        var source = connector.getDirectLinked(parameters, options, null);
        assertThat(source).isInstanceOf(AMQPConnectionReader.class);
    }

    @Test
    public void Check_instance_of_sequential() {
        var options = KOptions.DEFAULT;
        var connector = new AMQPConnectorSource(parameters, options);
        var source = connector.getSequentialComponent(null, parameters, options, null);
        assertThat(source).isInstanceOf(SequentialComponentSource.class);
        assertThat(source).matches(it -> it.getSource() instanceof AMQPConnectionReader);
    }

    @Test
    public void Check_instance_of_parallels() {
        var options = KOptions.DEFAULT;
        var connector = new AMQPConnectorSource(parameters, options);
        var source = connector.getParallelComponents(null, parameters, options, null);
        assertThat(source).isInstanceOf(ParallelComponents.class);
        assertThat(source).hasOnlyElementsOfType(ParallelComponentSource.class).hasSize(options.sourceThread());
        assertThat(source).allMatch(it -> ((ParallelComponentSource) it).getSource() instanceof AMQPConnectionReader);
    }

}
