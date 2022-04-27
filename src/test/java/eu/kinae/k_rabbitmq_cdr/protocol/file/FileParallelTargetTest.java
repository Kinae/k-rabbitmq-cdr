package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTargetTest;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileParallelTargetTest extends AbstractComponentTargetTest {

    private static final int CONSUMERS = 3;

    @TempDir
    protected static Path tempDir;

    @Test
    @Override
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var emptyQueue = new SharedQueue(ProcessType.PARALLEL);
        try(var target = mock(FileWriter.class)) {
            var executor = Executors.newFixedThreadPool(CONSUMERS);
            var callables = IntStream.range(0, CONSUMERS)
                    .mapToObj(integer -> new FileParallelTarget(emptyQueue, target, status))
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
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var sharedQueue = new SharedQueue(ProcessType.PARALLEL);
        for(KMessage message : MESSAGES)
            sharedQueue.push(message);

        try(var target = new FileWriter(tempDir)) {
            var executor = Executors.newFixedThreadPool(CONSUMERS);
            var callables = IntStream.range(0, CONSUMERS)
                    .mapToObj(integer -> new FileParallelTarget(sharedQueue, target, status))
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
        try(FileReader target = new FileReader(tempDir)) {
            Set<KMessage> set = new HashSet<>(MESSAGES);

            var kMessage = target.pop();
            while(kMessage != null) {
                assertThat(set.contains(kMessage)).isTrue();
                kMessage = target.pop();
            }
        }
    }
}
