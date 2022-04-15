package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AMQPParallelSourceTest extends AMQPAbstractComponentSourceTest {

    @Override
    protected Target getTarget() {
        return mock(SharedQueue.class);
    }

    @Override
    protected AMQPComponent getComponent(String queue, Target target, KOptions options) throws Exception {
        return new AMQPSequentialSource(new AMQPConnection(buildAMQPURI(rabbitmq), queue), (SharedQueue) target, options);
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new SharedQueue(ProcessType.PARALLEL);
            var component = new AMQPSequentialSource(new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(KMessage message : MESSAGES) {
                assertThat(target.pop().body()).isEqualTo(message.body());
            }
        }
    }

    @Test
    public void Start_source_in_single_thread_and_wait_at_most_60sec_to_consume_all_messages() throws Exception {
        var status = mock(SharedStatus.class);
        try(var target = new SharedQueue(ProcessType.PARALLEL);
            var component = new AMQPParallelSource(new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q), target, status, KOptions.DEFAULT)) {

            Future<?> future = Executors.newSingleThreadExecutor().submit(component);
            Awaitility.await().atMost(60, TimeUnit.SECONDS).until(future::isDone);

            assertThat(target.size()).isEqualTo(MESSAGES.size());
            assertThat(status.isConsumerAlive()).isFalse();
            Mockito.verify(status, Mockito.times(1)).notifySourceConsumerIsDone();
            for(KMessage message : MESSAGES) {
                KMessage actual = target.pop();
                assertThat(actual.body()).isEqualTo(message.body());
            }
        }

    }
}
