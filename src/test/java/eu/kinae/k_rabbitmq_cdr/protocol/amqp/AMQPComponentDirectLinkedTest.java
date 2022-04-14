package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AMQPComponentDirectLinkedTest {

    public static final String EMPTY_SOURCE_Q = "empty-source-q";
    public static final String SOURCE_Q = "source-q";
    public static final String TARGET_Q = "target-q";
    public static final List<KMessage> EMPTY_LIST = Collections.emptyList();
    public static final List<KMessage> MESSAGES = Stream.of(new Integer[] { 0, 1, 2, 3, 4 }).map(it -> new KMessage("TEST_" + it)).collect(Collectors.toList());

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(EMPTY_SOURCE_Q)
            .withQueue(SOURCE_Q)
            .withQueue(TARGET_Q);

    @BeforeAll
    public static void beforeAll() throws Exception {
        try(AMQPConnection sourceConnection = new AMQPConnection(buildAMQPURI(), SOURCE_Q)) {
            for(KMessage message : MESSAGES) {
                sourceConnection.push(message);
            }
        }
    }

    protected static String buildAMQPURI() {
        return String.format("amqp://%s:%s@%s:%d/%s", rabbitmq.getAdminUsername(), rabbitmq.getAdminPassword(), rabbitmq.getHost(), rabbitmq.getFirstMappedPort(), "%2F");
    }

    @Test
    public void Use_options_to_transfer_one_messages() throws Exception {
        KOptions options = new KOptions(1);
        try(AMQPConnection sConnection = new AMQPConnection(buildAMQPURI(), SOURCE_Q)) {
            Target target = Mockito.mock(Target.class);
            AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(sConnection, target, options);

            long actual = component.consumeNProduce();

            Assertions.assertThat(actual).isEqualTo(options.maxMessage());
            Mockito.verify(target, Mockito.times(1)).push(Mockito.any());
        }
    }

    @Test
    public void Use_options_to_transfer_a_subset_of_messages() throws Exception {
        KOptions options = new KOptions(MESSAGES.size() / 2);
        try(AMQPConnection sConnection = new AMQPConnection(buildAMQPURI(), SOURCE_Q)) {
            Target target = Mockito.mock(Target.class);
            AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(sConnection, target, options);

            long actual = component.consumeNProduce();

            Assertions.assertThat(actual).isEqualTo(options.maxMessage());
            Mockito.verify(target, Mockito.times((int) options.maxMessage())).push(Mockito.any());
        }
    }

    @Test
    public void Use_default_options_to_consume_and_produce_all_messages() throws Exception {
        try(AMQPConnection sConnection = new AMQPConnection(buildAMQPURI(), SOURCE_Q)) {
            Target target = Mockito.mock(Target.class);
            AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(sConnection, target);

            long actual = component.consumeNProduce();

            Assertions.assertThat(actual).isEqualTo(MESSAGES.size());
            Mockito.verify(target, Mockito.times(MESSAGES.size())).push(Mockito.any());
        }
    }

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        try(AMQPConnection sConnection = new AMQPConnection(buildAMQPURI(), EMPTY_SOURCE_Q)) {
            Target target = Mockito.mock(Target.class);
            AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(sConnection, target);

            long actual = component.consumeNProduce();

            Assertions.assertThat(actual).isEqualTo(0);
            Mockito.verify(target, Mockito.times(0)).push(Mockito.any());
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(AMQPConnection sConnection = new AMQPConnection(buildAMQPURI(), SOURCE_Q);
            AMQPConnection tConnection = new AMQPConnection(buildAMQPURI(), TARGET_Q)) {
            AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(sConnection, tConnection);

            long actual = component.consumeNProduce();

            Assertions.assertThat(actual).isEqualTo(MESSAGES.size());
            for(KMessage message : MESSAGES) {
                Assertions.assertThat(tConnection.pop().body()).isEqualTo(message.body());
            }
        }
    }
}
