package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
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
        return new AMQPParallelSource((AMQPConnection) source, (SharedQueue) target, options, new SharedStatus(options));
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new SharedQueue(ProcessType.PARALLEL);
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            assertThatSourceContainsAllMessagesSorted(target);
        }
    }

    @Test
    public void Start_source_in_single_thread_and_wait_at_most_60sec_to_consume_all_messages() throws Exception {
        var status = mock(SharedStatus.class);
        try(var target = new SharedQueue(ProcessType.PARALLEL);
            var component = new AMQPParallelSource(getSource(), target, KOptions.DEFAULT, status)) {

            Future<?> future = Executors.newSingleThreadExecutor().submit(component);
            Awaitility.await().atMost(60, TimeUnit.SECONDS).until(future::isDone);

            assertThat(target.size()).isEqualTo(MESSAGES.size());
            assertThat(status.isConsumerAlive()).isFalse();
            verify(status, times(1)).notifySourceConsumerIsDone();
            assertThatSourceContainsAllMessagesSorted(target);
        }
    }
}
