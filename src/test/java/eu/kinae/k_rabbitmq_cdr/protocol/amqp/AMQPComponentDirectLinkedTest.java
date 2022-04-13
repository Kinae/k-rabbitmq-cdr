package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AMQPComponentDirectLinkedTest {

    private static final String SOURCE_Q = "source-q";
    private static final String TARGET_Q = "target-q";
    private static final String[] MESSAGES = new String[] { "TEST_1", "TEST_2", "TEST_3", "TEST_4", "TEST_5" };

    @Container
    public final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(SOURCE_Q)
            .withQueue(TARGET_Q);

    @Test
    public void Use_options_to_transfer_a_subset_of_messages() throws Exception {
        String amqpURI = buildAMQPURI();
        try(AMQPConnection sourceConnection = new AMQPConnection(amqpURI, SOURCE_Q)) {
            for(String message : MESSAGES) {
                sourceConnection.basicPublish(null, message.getBytes());
            }
        }

        long maxMessage = 1;
        try(AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(buildKParameters(amqpURI), new KOptions(maxMessage))) {
            long actual = component.consumeNProduce();
            Assertions.assertThat(actual).isEqualTo(maxMessage);
        }
    }

    @Test
    public void Use_options_to_transfer_all_messages() throws Exception {
        String amqpURI = buildAMQPURI();
        try(AMQPConnection sourceConnection = new AMQPConnection(amqpURI, SOURCE_Q)) {
            for(String message : MESSAGES) {
                sourceConnection.basicPublish(null, message.getBytes());
            }
        }

        long maxMessage = 0;
        try(AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(buildKParameters(amqpURI), new KOptions(maxMessage))) {
            Assertions.assertThat(component.consumeNProduce()).isEqualTo(MESSAGES.length);
        }
    }

    @Test
    public void Transferred_messages_are_equal_to_originals() throws Exception {
        String amqpURI = buildAMQPURI();
        try(AMQPConnection sourceConnection = new AMQPConnection(amqpURI, SOURCE_Q)) {
            for(String message : MESSAGES) {
                sourceConnection.basicPublish(null, message.getBytes());
            }
        }

        try(AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(buildKParameters(amqpURI), new KOptions())) {
            component.consumeNProduce();
        }

        try(AMQPConnection targetConnection = new AMQPConnection(amqpURI, TARGET_Q)) {
            for(String message : MESSAGES) {
                Assertions.assertThat(targetConnection.basicGet().getBody()).isEqualTo(message.getBytes());
            }
        }
    }

    private String buildAMQPURI() {
        return String.format("amqp://%s:%s@%s:%d/%s", rabbitmq.getAdminUsername(), rabbitmq.getAdminPassword(), rabbitmq.getHost(), rabbitmq.getFirstMappedPort(), "%2F");
    }

    private KParameters buildKParameters(String uri) {
        return new KParameters(SupportedType.AMQP, uri, SOURCE_Q, SupportedType.AMQP, uri, TARGET_Q);
    }

}
