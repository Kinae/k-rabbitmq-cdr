package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractComponentSourceTest extends AbstractComponentTest {

    protected abstract Source getEmptySource() throws Exception;

    protected abstract Source getSource() throws Exception;

    protected abstract Target getMockTarget() throws Exception;

    protected AbstractComponent getComponent(Source source, Target target) {
        return getComponent(source, target, KOptions.DEFAULT);
    }

    protected abstract AbstractComponent getComponent(Source source, Target target, KOptions options);

    @Test
    public void Use_options_to_transfer_one_messages() throws Exception {
        var options = new KOptions(1);
        try(var target = getMockTarget();
            var component = getComponent(getSource(), target, options)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(options.maxMessage());
            verify(target, times(1)).push(any(), eq(options));
        }
    }

    @Test
    public void Use_options_to_transfer_a_subset_of_messages() throws Exception {
        var options = new KOptions(MESSAGES.size() / 2);
        try(var target = getMockTarget();
            var component = getComponent(getSource(), target, options)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(options.maxMessage());
            verify(target, times(options.maxMessage())).push(any(), eq(options));
        }
    }

    @Test
    public void Use_default_options_to_consume_and_produce_all_messages() throws Exception {
        var options = KOptions.DEFAULT;
        try(var target = getMockTarget();
            var component = getComponent(getSource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
            verify(target, times(MESSAGES.size())).push(any(), eq(options));
        }
    }

    @Test
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var options = KOptions.DEFAULT;
        try(var target = getMockTarget();
            var component = getComponent(getEmptySource(), target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(target, times(0)).push(any(), eq(options));
        }
    }

}
