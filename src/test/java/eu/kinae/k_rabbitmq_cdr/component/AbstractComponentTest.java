package eu.kinae.k_rabbitmq_cdr.component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractComponentTest {

    protected static final int CONSUMERS = 3;
    private static final int NUMBER_OF_MESSAGES = 30;

    public static final List<KMessage> MESSAGES = IntStream.range(0, NUMBER_OF_MESSAGES).boxed()
            .map(it -> new KMessage(new AMQP.BasicProperties().builder().appId("APPID_" + it).build(), "TEST_" + it, it))
            .collect(Collectors.toList());
    private static final List<KMessage> MESSAGES_BODY_ONLY = IntStream.range(0, NUMBER_OF_MESSAGES).boxed()
        .map(it -> new KMessage(null, "TEST_" + it, it))
        .collect(Collectors.toList());

    public static final Set<KMessage> MESSAGES_SET = new LinkedHashSet<>(MESSAGES);
    private static final Set<KMessage> MESSAGES_BODY_ONY_SET = new LinkedHashSet<>(MESSAGES_BODY_ONLY);

    protected void assertThatContainsAllMessages(Source source, KOptions options) throws Exception {
        if(options.sorted()) {
            assertThatContainsAllMessagesSorted(source, options);
        } else {
            assertThatContainsAllMessagesUnsorted(source, options);
        }
    }

    private void assertThatContainsAllMessagesSorted(Source source, KOptions options) throws Exception {
        for(var message : MESSAGES) {
            var actualMessage = source.pop(options);
            if(options.bodyOnly()) {
                assertThat(actualMessage.properties()).isNull();
                assertThat(actualMessage.body()).isEqualTo(message.body());
            } else {
                assertThat(actualMessage).isEqualTo(message);
            }
        }
    }

    private void assertThatContainsAllMessagesUnsorted(Source source, KOptions options) throws Exception {
        var actualMessage = source.pop(options);
        while(actualMessage != null) {
            if(options.bodyOnly()) {
                assertThat(actualMessage.properties()).isNull();
                assertThat(MESSAGES_BODY_ONY_SET.contains(actualMessage)).isTrue();
            } else {
                assertThat(MESSAGES_SET.contains(actualMessage)).isTrue();
            }
            actualMessage = source.pop(options);
        }
    }
}
