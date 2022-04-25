package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AMQPSequentialSourceTest extends AMQPAbstractComponentSourceTest {

    @Override
    protected Target getTarget() {
        return mock(SharedQueue.class);
    }

    @Override
    protected AbstractComponentSource getComponent(String queue, Target target, KOptions options) throws Exception {
        return new AMQPSequentialSource(new AMQPConnection(buildAMQPURI(rabbitmq), queue), (SharedQueue) target, options);
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new SharedQueue(ProcessType.SEQUENTIAL);
            var component = new AMQPSequentialSource(new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(KMessage message : MESSAGES) {
                assertThat(target.pop().body()).isEqualTo(message.body());
            }
        }
    }

}
