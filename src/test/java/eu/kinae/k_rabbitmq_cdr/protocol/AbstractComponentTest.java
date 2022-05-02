package eu.kinae.k_rabbitmq_cdr.protocol;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractComponentTest {

    public static final List<KMessage> MESSAGES = IntStream.range(0, 30).boxed()
            .map(it -> new KMessage(new AMQP.BasicProperties().builder().appId("APPID_" + it).build(), "TEST_" + it, it))
            .collect(Collectors.toList());

    public static final Set<KMessage> MESSAGES_SET = new HashSet<>(MESSAGES);

    protected void assertThatSourceContainsAllMessagesSorted(Source source) throws Exception {
        for(var message : MESSAGES) {
            assertThat(source.pop()).isEqualTo(message);
        }
    }

    protected void assertThatSourceContainsAllMessagesUnsorted(Source source) throws Exception {
        var kMessage = source.pop();
        while(kMessage != null) {
            assertThat(MESSAGES_SET.contains(kMessage)).isTrue();
            kMessage = source.pop();
        }

    }
}
