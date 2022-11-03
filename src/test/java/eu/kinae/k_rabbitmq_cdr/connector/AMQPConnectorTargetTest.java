package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnectionWriter;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AMQPConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@Testcontainers
public class AMQPConnectorTargetTest {

    private static final String TARGET_Q = "target-q";

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
        .withQueue(TARGET_Q);

    private final KParameters parameters = new KParameters(null, null, null, SupportedType.AMQP, AMQPUtils.buildAMQPURI(rabbitmq), TARGET_Q, null, null, null, null, null, null, null);

    @Test
    public void Check_constructor() {
        var connector = new AMQPConnectorTarget(parameters);
        assertThat(connector.getSupportedType()).isEqualTo(SupportedType.AMQP);
        assertThatNoException().isThrownBy(connector::close);
    }

    @Test
    public void Check_instance_of_direct_linked() {
        var connector = new AMQPConnectorTarget(parameters);
        var source = connector.getDirectLinked(parameters, null);
        assertThat(source).isInstanceOf(AMQPConnectionWriter.class);
    }

    @Test
    public void Check_instance_of_sequential() {
        var options = KOptions.DEFAULT;
        var connector = new AMQPConnectorTarget(parameters);
        var target = connector.getSequentialComponent(null, parameters, options, null);
        assertThat(target).isInstanceOf(SequentialComponentTarget.class);
        assertThat(target).matches(it -> it.getTarget() instanceof AMQPConnectionWriter);
    }

    @Test
    public void Check_instance_of_parallels() {
        var options = KOptions.DEFAULT;
        var connector = new AMQPConnectorTarget(parameters);
        var target = connector.getParallelComponents(null, parameters, options, null);
        assertThat(target).isInstanceOf(ParallelComponents.class);
        assertThat(target).hasOnlyElementsOfType(ParallelComponentTarget.class).hasSize(options.targetThread());
        assertThat(target).allMatch(it -> ((ParallelComponentTarget) it).getTarget() instanceof AMQPConnectionWriter);
    }

}
