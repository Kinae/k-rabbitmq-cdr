package eu.kinae.k_rabbitmq_cdr.component.amqp;

import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils.buildAMQPConnection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AMQPSequentialTargetTest extends AMQPAbstractComponentTargetTest {

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.SEQUENTIAL);
    }

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var options = KOptions.DEFAULT;
        var emptyQueue = getSharedQueue();
        try(var target = mock(AMQPConnectionWriter.class);
            var component = new SequentialComponentTarget(emptyQueue, target, options)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(target, times(0)).push(any());
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var options = KOptions.DEFAULT;
        var sharedQueue = getSharedQueue();
        for(var message : MESSAGES) {
            sharedQueue.push(message);
        }

        try(var connection = buildAMQPConnection(rabbitmq);
            var component = new SequentialComponentTarget(sharedQueue, new AMQPConnectionWriter(connection, TARGET_Q), options)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(var target = new AMQPConnectionReader(buildAMQPConnection(rabbitmq), TARGET_Q)) {
            assertThatContainsAllMessages(target, options);
        }
    }
}
