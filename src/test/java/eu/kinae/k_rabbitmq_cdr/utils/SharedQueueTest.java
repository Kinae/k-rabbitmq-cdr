package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.List;

import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SharedQueueTest {

    @Test
    public void Sequential_queue_push_and_pop_messages() throws Exception {
        List<KMessage> messages = List.of(wrapResponse(2), wrapResponse(1), wrapResponse(0));
        SharedQueue queue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(KMessage message : messages) {
            queue.push(message);
        }
        Assertions.assertThat(queue.size()).isEqualTo(messages.size());
        for(int i = 0; i < messages.size(); ++i) {
            Assertions.assertThat(queue.pop()).isEqualTo(messages.get(i));
        }

        Assertions.assertThat(queue.size()).isEqualTo(0);
        Assertions.assertThat(queue.pop()).isNull();
    }

    @Test
    public void Parallel_queue_push_and_pop_messages() throws Exception {
        List<KMessage> messages = List.of(wrapResponse(2), wrapResponse(1), wrapResponse(0));
        SharedQueue queue = new SharedQueue(ProcessType.PARALLEL);
        for(KMessage message : messages) {
            queue.push(message);
        }
        Assertions.assertThat(queue.size()).isEqualTo(messages.size());
        for(int i = 0; i < messages.size(); ++i) {
            Assertions.assertThat(queue.pop()).isEqualTo(messages.get(i));
        }

        Assertions.assertThat(queue.size()).isEqualTo(0);
        Assertions.assertThat(queue.pop()).isNull();
    }

    private KMessage wrapResponse(int i) {
        return new KMessage(null, null, i, i);
    }

}
