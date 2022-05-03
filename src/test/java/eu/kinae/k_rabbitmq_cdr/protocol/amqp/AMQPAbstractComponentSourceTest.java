package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;

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
        try(var sourceConnection = new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q)) {
            for(var message : MESSAGES) {
                sourceConnection.push(message);
            }
        }
    }

    @Override
    protected AMQPConnection getEmptySource() {
        return new AMQPConnection(buildAMQPURI(rabbitmq), EMPTY_SOURCE_Q);
    }

    @Override
    protected AMQPConnection getSource() {
        return new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q);
    }

}
