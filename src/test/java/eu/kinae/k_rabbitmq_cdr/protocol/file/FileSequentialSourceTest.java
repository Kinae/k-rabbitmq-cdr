package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileSequentialSourceTest extends FileAbstractComponentSourceTest {

    @Override
    protected AbstractComponent getComponent(Source source, Target target, KOptions options) {
        return new FileSequentialSource((FileReader) source, (SharedQueue) target, options);
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        try(var target = new SharedQueue(ProcessType.SEQUENTIAL);
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            for(KMessage message : MESSAGES) {
                assertThat(target.pop()).isEqualTo(message);
            }
        }
    }

}
