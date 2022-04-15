package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import static eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPUtils.buildAMQPURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Testcontainers
public class AMQPComponentDirectLinkedTest extends AMQPAbstractComponentSourceTest {

    @Override
    protected Target getTarget() {
        return mock(AMQPConnection.class);
    }

    @Override
    protected AMQPComponent getComponent(String queue, Target target, KOptions options) throws Exception {
        return new AMQPComponentDirectLinked(new AMQPConnection(buildAMQPURI(rabbitmq), queue), (AMQPConnection) target, options);
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new AMQPConnection(buildAMQPURI(rabbitmq), TARGET_Q);
            var component = new AMQPComponentDirectLinked(new AMQPConnection(buildAMQPURI(rabbitmq), SOURCE_Q), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(KMessage message : MESSAGES) {
                assertThat(target.pop().body()).isEqualTo(message.body());
            }
        }
    }
}
