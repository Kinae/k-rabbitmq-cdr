package eu.kinae.k_rabbitmq_cdr.component.aws;

import java.util.ArrayList;
import java.util.UUID;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AWS_S3ParallelTargetTest extends AWS_S3AbstractComponentTargetTest {

    private static final int CONSUMERS = 3;

    @Test
    @Override
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var options = KOptions.DEFAULT;
        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var emptyQueue = new SharedQueue(ProcessType.PARALLEL);
        try(var target = mock(AWS_S3Writer.class)) {
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

            verify(target, times(0)).push(any(), eq(options));
        }
    }

    @Test
    @Override
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var options = KOptions.DEFAULT;
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var status = mock(SharedStatus.class);
        when(status.isConsumerAlive()).thenReturn(false);

        var sharedQueue = new SharedQueue(ProcessType.PARALLEL);
        for(var message : MESSAGES)
            sharedQueue.push(message, options);

        try(var target = new AWS_S3Writer(s3, bucket, PREFIX)) {
            var executor = Executors.newFixedThreadPool(CONSUMERS);
            var callables = IntStream.range(0, CONSUMERS)
                .mapToObj(ignored -> new ParallelComponentTarget(sharedQueue, target, options, status))
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
        try(var target = new AWS_S3Reader(s3, bucket, PREFIX, options)) {
            assertThatSourceContainsAllMessagesUnsorted(target);
        }
    }
}
