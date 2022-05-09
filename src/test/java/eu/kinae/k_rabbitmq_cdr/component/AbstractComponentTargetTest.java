package eu.kinae.k_rabbitmq_cdr.component;

import org.junit.jupiter.api.Test;

public abstract class AbstractComponentTargetTest extends AbstractComponentTest {

    @Test
    public abstract void Consume_from_empty_queue_produce_nothing() throws Exception;

    @Test
    public abstract void Produced_messages_are_equal_to_consumed_messages() throws Exception;

}
