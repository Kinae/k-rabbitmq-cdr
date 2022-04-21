package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
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
import static org.mockito.Mockito.when;

@Testcontainers
public class AMQPParallelTargetTest {

    private static final int CONSUMERS = 3;
    private static final String TARGET_Q = "target-q";
    private static final List<KMessage> MESSAGES = IntStream.range(0, 2000).boxed().map(it -> new KMessage("TEST_" + it)).collect(Collectors.toList());

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(TARGET_Q);

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var emptyQueue = new SharedQueue(ProcessType.PARALLEL);
        try(var connection = mock(AMQPConnection.class)) {
            var executor = Executors.newFixedThreadPool(CONSUMERS);

            var callables = IntStream.range(0, CONSUMERS)
                    .mapToObj(integer -> new AMQPParallelTarget(connection, emptyQueue, status))
                    .collect(Collectors.toCollection(ArrayList::new));
            var futures = executor.invokeAll(callables, 60, TimeUnit.SECONDS);
            assertThat(futures.stream().filter(Future::isDone).count()).isEqualTo(CONSUMERS);
            assertThat(futures.stream().mapToLong(it -> {
                try {
                    return it.get();
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }).sum()).isEqualTo(emptyQueue.size());

            assertThat(connection.pop()).isNull();
            verify(connection, times(0)).push(any());
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var sharedQueue = new SharedQueue(ProcessType.PARALLEL);
        for(KMessage message : MESSAGES)
            sharedQueue.push(message);

        try(var connection = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {
            var executor = Executors.newFixedThreadPool(CONSUMERS);

            var callables = IntStream.range(0, CONSUMERS)
                    .mapToObj(integer -> new AMQPParallelTarget(connection, sharedQueue, status))
                    .collect(Collectors.toCollection(ArrayList::new));

            var futures = executor.invokeAll(callables, 60, TimeUnit.SECONDS);
            assertThat(futures.stream().filter(Future::isDone).count()).isEqualTo(CONSUMERS);
            assertThat(futures.stream().mapToLong(it -> {
                try {
                    return it.get();
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }).sum()).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(AMQPConnection targetConnection = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {
            Set<KMessage> set = new HashSet<>(MESSAGES);

            var kMessage = targetConnection.pop();
            while(kMessage != null) {
                assertThat(set.contains(kMessage)).isTrue();
                kMessage = targetConnection.pop();
            }
        }
    }
}
