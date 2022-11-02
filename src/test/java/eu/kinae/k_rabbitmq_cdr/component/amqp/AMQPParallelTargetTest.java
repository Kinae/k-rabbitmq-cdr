package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.junit.jupiter.api.Test;

import static eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils.buildAMQPConnection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AMQPParallelTargetTest extends AMQPAbstractComponentTargetTest {

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.PARALLEL);
    }

    @Test
    @Override
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var options = KOptions.DEFAULT;
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var emptyQueue = getSharedQueue();
        try(var target = mock(AMQPConnectionWriter.class)) {
            var executor = Executors.newFixedThreadPool(CONSUMERS);
            var callables = IntStream.range(0, CONSUMERS)
                .mapToObj(integer -> new ParallelComponentTarget(emptyQueue, target, options, status))
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

            verify(target, times(0)).push(any());
        }
    }

    @Test
    @Override
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var options = KOptions.DEFAULT;
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var sharedQueue = getSharedQueue();
        for(var message : MESSAGES)
            sharedQueue.push(message);

        try(var connection = buildAMQPConnection(rabbitmq)) {
            var executor = Executors.newFixedThreadPool(CONSUMERS);
            var callables = IntStream.range(0, CONSUMERS)
                .mapToObj(integer -> new ParallelComponentTarget(sharedQueue, new AMQPConnectionWriter(connection, TARGET_Q), options, status))
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
        try(var source = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), TARGET_Q)) {
            assertThatContainsAllMessages(source, options);
        }
    }

}
