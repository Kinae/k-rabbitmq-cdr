package eu.kinae.k_rabbitmq_cdr.component.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AMQPSequentialTargetTest extends AMQPAbstractComponentTargetTest {

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var options = KOptions.DEFAULT;
        var emptyQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        try(var target = mock(AMQPConnectionWriter.class);
            var component = new AMQPSequentialTarget(emptyQueue, target, options)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(target, times(0)).push(any(), eq(options));
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var options = KOptions.DEFAULT;
        var sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(var message : MESSAGES)
            sharedQueue.push(message, options);

        try(var component = new AMQPSequentialTarget(sharedQueue, new AMQPConnectionWriter(buildAMQPURI(rabbitmq), TARGET_Q), options)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(var source = new AMQPConnectionReader(buildAMQPURI(rabbitmq), TARGET_Q)) {
            assertThatSourceContainsAllMessagesSorted(source);
        }
    }
}
