package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AMQPSequentialSourceTest extends AMQPAbstractComponentSourceTest {

    @Override
    protected SharedQueue getMockTarget() {
        return mock(SharedQueue.class);
    }

    @Override
    protected AbstractComponentSource getComponent(Source source, Target target, KOptions options) {
        return new AMQPSequentialSource((AMQPConnection) source, (SharedQueue) target, options);
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new SharedQueue(ProcessType.SEQUENTIAL);
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(var message : MESSAGES) {
                assertThat(target.pop()).isEqualTo(message);
            }
        }
    }

}
