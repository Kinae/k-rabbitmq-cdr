package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AMQPParallelSourceTest extends AMQPAbstractComponentSourceTest {

    @Override
    protected SharedQueue getMockTarget() {
        return mock(SharedQueue.class);
    }

    @Override
    protected AbstractComponentSource getComponent(Source source, Target target, KOptions options) {
        return new AMQPParallelSource((AMQPConnection) source, (SharedQueue) target, new SharedStatus(), options);
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new SharedQueue(ProcessType.PARALLEL);
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(KMessage message : MESSAGES) {
                assertThat(target.pop()).isEqualTo(message);
            }
        }
    }

    @Test
    public void Start_source_in_single_thread_and_wait_at_most_60sec_to_consume_all_messages() throws Exception {
        var status = mock(SharedStatus.class);
        try(var target = new SharedQueue(ProcessType.PARALLEL);
            var component = new AMQPParallelSource(getSource(), target, status, KOptions.DEFAULT)) {

            Future<?> future = Executors.newSingleThreadExecutor().submit(component);
            Awaitility.await().atMost(60, TimeUnit.SECONDS).until(future::isDone);

            assertThat(target.size()).isEqualTo(MESSAGES.size());
            assertThat(status.isConsumerAlive()).isFalse();
            Mockito.verify(status, Mockito.times(1)).notifySourceConsumerIsDone();
            for(KMessage message : MESSAGES) {
                KMessage actual = target.pop();
                assertThat(actual).isEqualTo(message);
            }
        }
    }
}
