package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AMQPSequentialTargetTest extends AMQPAbstractComponentTargetTest {

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var emptyQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        try(var target = mock(AMQPConnection.class);
            var component = new AMQPSequentialTarget(emptyQueue, target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            assertThat(target.pop()).isNull();
            verify(target, times(0)).push(any());
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(var message : MESSAGES)
            sharedQueue.push(message);

        try(var component = new AMQPSequentialTarget(sharedQueue, new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q))) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(var target = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q)) {
            for(var message : MESSAGES) {
                assertThat(target.pop()).isEqualTo(message);
            }
        }
    }
}
