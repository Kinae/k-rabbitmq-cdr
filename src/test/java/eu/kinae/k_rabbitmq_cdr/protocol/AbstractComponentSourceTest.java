package eu.kinae.k_rabbitmq_cdr.protocol;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractComponentSourceTest {

    public static final List<KMessage> MESSAGES = IntStream.range(0, 1000).boxed()
            .map(it -> new KMessage(new AMQP.BasicProperties().builder().appId("APPID_" + it).build(), "TEST_" + it, it))
            .collect(Collectors.toList());

    protected abstract Source getEmptySource() throws Exception;

    protected abstract Source getSource() throws Exception;

    protected abstract Target getMockTarget() throws Exception;

    protected AbstractComponent getComponent(Source source, Target target) {
        return getComponent(source, target, KOptions.DEFAULT);
    }

    protected abstract AbstractComponent getComponent(Source source, Target target, KOptions options);

    @Test
    public void Use_options_to_transfer_one_messages() throws Exception {
        KOptions options = new KOptions(1);
        try(var target = getMockTarget();
            var component = getComponent(getSource(), target, options)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(options.maxMessage());
            verify(target, times(1)).push(any());
        }
    }

    @Test
    public void Use_options_to_transfer_a_subset_of_messages() throws Exception {
        KOptions options = new KOptions(MESSAGES.size() / 2);
        try(var target = getMockTarget();
            var component = getComponent(getSource(), target, options)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(options.maxMessage());
            verify(component.getTarget(), times((int) options.maxMessage())).push(any());
        }
    }

    @Test
    public void Use_default_options_to_consume_and_produce_all_messages() throws Exception {
        try(var target = getMockTarget();
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            verify(component.getTarget(), times(MESSAGES.size())).push(any());
        }
    }

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        try(var target = getMockTarget();
            var component = getComponent(getEmptySource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(component.getTarget(), times(0)).push(any());
        }
    }

}
