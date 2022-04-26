package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
public class AMQPSequentialTargetTest {

    public static final String TARGET_Q = "target-q";
    public static final List<KMessage> MESSAGES = IntStream.range(0, 2000).boxed()
            .map(it -> new KMessage(new AMQP.BasicProperties().builder().appId("APPID_" + it).build(), "TEST_" + it))
            .collect(Collectors.toList());

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(TARGET_Q);

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        SharedQueue emptyQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        try(var target = mock(AMQPConnection.class);
            var component = new AMQPSequentialTarget(emptyQueue, target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            assertThat(target.pop()).isNull();
            verify(component.getTarget(), times(0)).push(any());
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(KMessage message : MESSAGES)
            sharedQueue.push(message);

        try(var component = new AMQPSequentialTarget(sharedQueue, new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q))) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(AMQPConnection targetConnection = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {
            for(KMessage message : MESSAGES) {
                assertThat(targetConnection.pop()).isEqualTo(message);
            }
        }
    }
}
