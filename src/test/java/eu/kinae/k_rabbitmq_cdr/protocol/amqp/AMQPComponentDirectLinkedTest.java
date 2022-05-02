package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import org.junit.jupiter.api.Test;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AMQPComponentDirectLinkedTest extends AMQPAbstractComponentSourceTest {

    @Override
    protected AMQPConnection getMockTarget() {
        return mock(AMQPConnection.class);
    }

    @Override
    protected AbstractComponent getComponent(Source source, Target target, KOptions options) {
        return new AMQPComponentDirectLinked((AMQPConnection) source, (AMQPConnection) target, options);
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q);
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(var message : MESSAGES) {
                assertThat(target.pop()).isEqualTo(message);
            }
        }
    }
}
