package eu.kinae.k_rabbitmq_cdr.utils;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SharedStatusTest {

    @Test
    public void Consumer_is_alive_by_default() {
        Assertions.assertThat(new SharedStatus().isConsumerAlive()).isTrue();
    }

    @Test
    public void Consumer_is_not_alive_after_notification() {
        var status = new SharedStatus();
        Assertions.assertThat(status.isConsumerAlive()).isTrue();
        status.notifySourceConsumerIsDone();
        Assertions.assertThat(status.isConsumerAlive()).isFalse();
    }

    @Test
    public void Total_message_is_set_to_max_from_options() {
        var options = new KOptions(42);
        var status = new SharedStatus(options);
        Assertions.assertThat(status.getTotal()).isEqualTo(options.maxMessage());
    }

}
