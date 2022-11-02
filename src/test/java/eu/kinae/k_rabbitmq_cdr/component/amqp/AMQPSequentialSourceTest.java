package eu.kinae.k_rabbitmq_cdr.component.amqp;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
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
        return new SequentialComponentSource(source, (SharedQueue) target, options);
    }

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.SEQUENTIAL);
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
}
