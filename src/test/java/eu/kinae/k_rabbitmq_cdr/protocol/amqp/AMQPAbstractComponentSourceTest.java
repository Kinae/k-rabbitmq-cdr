package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
public abstract class AMQPAbstractComponentSourceTest {

    public static final String EMPTY_SOURCE_Q = "empty-source-q";
    public static final String SOURCE_Q = "source-q";
    public static final String TARGET_Q = "target-q";
    public static final List<KMessage> MESSAGES = IntStream.range(0, 2000).boxed().map(it -> new KMessage("TEST_" + it)).collect(Collectors.toList());

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(EMPTY_SOURCE_Q)
            .withQueue(SOURCE_Q)
            .withQueue(TARGET_Q);

    @BeforeAll
    public static void beforeAll() throws Exception {
        try(AMQPConnection sourceConnection = new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q)) {
            for(KMessage message : MESSAGES) {
                sourceConnection.push(message);
            }
        }
    }

    protected abstract Target getTarget();

    protected abstract AMQPComponent getComponent(String queue, Target target, KOptions options) throws Exception;

    @Test
    public void Use_options_to_transfer_one_messages_2() throws Exception {
        KOptions options = new KOptions(1);
        try(var target = getTarget(); var component = getComponent(SOURCE_Q, target, options)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(options.maxMessage());
            verify(target, times(1)).push(any());
        }
    }

    @Test
    public void Use_options_to_transfer_one_messages() throws Exception {
        KOptions options = new KOptions(1);
        try(var target = getTarget(); var component = getComponent(SOURCE_Q, target, options)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(options.maxMessage());
            verify(component.getTarget(), times(1)).push(any());
        }
    }

    @Test
    public void Use_options_to_transfer_a_subset_of_messages() throws Exception {
        KOptions options = new KOptions(MESSAGES.size() / 2);
        try(var target = getTarget(); var component = getComponent(SOURCE_Q, target, options)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(options.maxMessage());
            verify(component.getTarget(), times((int) options.maxMessage())).push(any());
        }
    }

    @Test
    public void Use_default_options_to_consume_and_produce_all_messages() throws Exception {
        try(var target = getTarget(); var component = getComponent(SOURCE_Q, target, KOptions.DEFAULT)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            verify(component.getTarget(), times(MESSAGES.size())).push(any());
        }
    }

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        try(var target = getTarget(); var component = getComponent(EMPTY_SOURCE_Q, target, KOptions.DEFAULT)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(component.getTarget(), times(0)).push(any());
        }
    }

}
