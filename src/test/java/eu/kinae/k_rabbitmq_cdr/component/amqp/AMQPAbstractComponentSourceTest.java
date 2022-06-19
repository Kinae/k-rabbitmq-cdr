package eu.kinae.k_rabbitmq_cdr.component.amqp;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSourceTest;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils.buildAMQPURI;

@Testcontainers
public abstract class AMQPAbstractComponentSourceTest extends AbstractComponentSourceTest {

    public static final String EMPTY_SOURCE_Q = "empty-source-q";
    public static final String SOURCE_Q = "source-q";
    public static final String TARGET_Q = "target-q";

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(EMPTY_SOURCE_Q)
            .withQueue(SOURCE_Q)
            .withQueue(TARGET_Q);

    @BeforeAll
    public static void beforeAll() throws Exception {
        try(var target = new AMQPConnectionWriter(buildAMQPURI(rabbitmq), SOURCE_Q)) {
            for(var message : MESSAGES) {
                target.push(message, KOptions.DEFAULT);
            }
        }
    }

    @Override
    protected AMQPConnectionReader getEmptySource() {
        return new AMQPConnectionReader(buildAMQPURI(rabbitmq), EMPTY_SOURCE_Q);
    }

    @Override
    protected AMQPConnectionReader getSource() {
        return new AMQPConnectionReader(buildAMQPURI(rabbitmq), SOURCE_Q);
    }

}
