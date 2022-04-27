package eu.kinae.k_rabbitmq_cdr.protocol;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.junit.jupiter.api.Test;

public abstract class AbstractComponentTargetTest {

    public static final List<KMessage> MESSAGES = IntStream.range(0, 1000).boxed()
            .map(it -> new KMessage(new AMQP.BasicProperties().builder().appId("APPID_" + it).build(), "TEST_" + it, it))
            .collect(Collectors.toList());

    @Test
    public abstract void Consume_from_empty_queue_produce_nothing() throws Exception;

    @Test
    public abstract void Produced_messages_are_equal_to_consumed_messages() throws Exception;

}
