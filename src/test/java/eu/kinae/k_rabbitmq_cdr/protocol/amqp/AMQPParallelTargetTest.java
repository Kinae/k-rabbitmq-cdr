package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.assertj.core.api.Assertions;
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

    public static final String TARGET_Q = "target-q";
    public static final List<KMessage> MESSAGES = IntStream.range(0, 2000).boxed().map(it -> new KMessage("TEST_" + it)).collect(Collectors.toList());

    @Container
    public static final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withQueue(TARGET_Q);

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        SharedQueue emptyQueue = new SharedQueue(ProcessType.PARALLEL);
        try(var target = mock(AMQPConnection.class)) {
            var executor = Executors.newFixedThreadPool(3);

            var callables = new ArrayList<Callable<Long>>();
            callables.add(new AMQPParallelTarget(target, emptyQueue, status));
            callables.add(new AMQPParallelTarget(target, emptyQueue, status));
            callables.add(new AMQPParallelTarget(target, emptyQueue, status));

            var futures = executor.invokeAll(callables, 60, TimeUnit.SECONDS);
            Assertions.assertThat(futures.stream().filter(Future::isDone).count()).isEqualTo(3L);
            Assertions.assertThat(futures.stream().mapToLong(it -> {
                try {
                    return it.get();
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }).sum()).isEqualTo(emptyQueue.size());

            assertThat(target.pop()).isNull();
            verify(target, times(0)).push(any());

            //            Future<?> future = Executors.newFixedThreadPool(3).submit(component);
            //            Awaitility.await().atMost(10, TimeUnit.SECONDS).until(future::isDone);

        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {

        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
        for(KMessage message : MESSAGES)
            sharedQueue.push(message);

        try(var connection = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {
            var executor = Executors.newFixedThreadPool(3);

            var callables = new ArrayList<Callable<Long>>();
            callables.add(new AMQPParallelTarget(connection, sharedQueue, status));
            callables.add(new AMQPParallelTarget(connection, sharedQueue, status));
            callables.add(new AMQPParallelTarget(connection, sharedQueue, status));

            var futures = executor.invokeAll(callables, 60, TimeUnit.SECONDS);
            Assertions.assertThat(futures.stream().filter(Future::isDone).count()).isEqualTo(3L);
            Assertions.assertThat(futures.stream().mapToLong(it -> {
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
