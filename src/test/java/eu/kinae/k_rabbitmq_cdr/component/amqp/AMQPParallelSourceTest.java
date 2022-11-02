package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AMQPParallelSourceTest extends AMQPAbstractComponentSourceTest {

    @Override
    protected SharedQueue getMockTarget() {
        return mock(SharedQueue.class);
    }

    @Override
    protected AbstractComponentSource getComponent(Source source, Target target, KOptions options) {
        return new ParallelComponentSource(source, (SharedQueue) target, options, new SharedStatus(options));
    }

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.PARALLEL);
    }


    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var options = KOptions.SORTED;
        var target = getSharedQueue();
        try(var component = getComponent(getSource(options), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatContainsAllMessages(target, options);
        }
    }

    @Test
    public void Start_source_in_single_thread_and_wait_at_most_60sec_to_consume_all_sorted_messages() throws Exception {
        var options = KOptions.SORTED;
        var status = mock(SharedStatus.class);
        var target = getSharedQueue();
        try(var component = new ParallelComponentSource(getSource(options), target, options, status)) {

            Future<?> future = Executors.newSingleThreadExecutor().submit(component);
            Awaitility.await().atMost(60, TimeUnit.SECONDS).until(future::isDone);

            assertThat(target.size()).isEqualTo(MESSAGES.size());
            assertThat(status.isConsumerAlive()).isFalse();
            verify(status, times(1)).notifySourceConsumerIsDone();
            assertThatContainsAllMessages(target, options);
        }
    }

    @Test
    public void Start_source_in_multi_thread_and_wait_at_most_60sec_to_consume_all_messages() throws Exception {
        var options = KOptions.DEFAULT;
        var status = mock(SharedStatus.class);
        var target = getSharedQueue();
        try(var source = getSource(options)) {

            var executor = Executors.newFixedThreadPool(CONSUMERS);
            var callables = IntStream.range(0, CONSUMERS)
                .mapToObj(ignored -> new ParallelComponentSource(source, target, options, status))
                .collect(Collectors.toCollection(ArrayList::new));
            var futures = executor.invokeAll(callables, 60, TimeUnit.SECONDS);

            assertThat(futures.stream().filter(Future::isDone).count()).isEqualTo(3);
            assertThat(futures.stream().mapToLong(it -> {
                try {
                    return it.get();
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }).sum()).isEqualTo(MESSAGES.size());

            assertThat(target.size()).isEqualTo(MESSAGES.size());
            assertThat(status.isConsumerAlive()).isFalse();
            verify(status, times(CONSUMERS)).notifySourceConsumerIsDone();
            assertThatContainsAllMessages(target, options);
        }
    }

}
