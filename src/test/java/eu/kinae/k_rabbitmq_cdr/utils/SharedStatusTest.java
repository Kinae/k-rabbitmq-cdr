package eu.kinae.k_rabbitmq_cdr.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SharedStatusTest {

    @Test
    public void testConsumerIsAliveIsTrueByDefault() {
        Assertions.assertThat(new SharedStatus().isConsumerAlive()).isTrue();
    }

    @Test
    public void testConsumerIsAliveIsFalseAfterNotifying() {
        SharedStatus status = new SharedStatus();
        status.notifySourceConsumerIsDone();
        Assertions.assertThat(status.isConsumerAlive()).isFalse();
    }

}
