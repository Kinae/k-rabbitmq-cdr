package eu.kinae.k_rabbitmq_cdr.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SharedStatusTest {

    @Test
    public void Consumer_is_alive_by_default() {
        Assertions.assertThat(new SharedStatus().isConsumerAlive()).isTrue();
    }

    @Test
    public void Consumer_is_not_alive_after_notification() {
        SharedStatus status = new SharedStatus();
        Assertions.assertThat(status.isConsumerAlive()).isTrue();
        status.notifySourceConsumerIsDone();
        Assertions.assertThat(status.isConsumerAlive()).isFalse();
    }

}
