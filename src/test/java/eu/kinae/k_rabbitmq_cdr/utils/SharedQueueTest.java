package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.AbstractSequentialList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SharedQueueTest {

    @Test
    public void testSequentialQueueIsAnInstanceOfSequentialList() {
        Assertions.assertThat(new SharedQueue(ProcessType.SEQUENTIAL).getBufferType()).isInstanceOf(AbstractSequentialList.class);
    }

    @Test
    public void testParallelQueueIsAnInstanceOfBlockingQueue() {
        Assertions.assertThat(new SharedQueue(ProcessType.PARALLEL).getBufferType()).isInstanceOf(BlockingQueue.class);
    }

    @Test
    public void testPushNPopFromSequentialQueue() throws InterruptedException {
        List<GetResponse> responses = List.of(wrapResponse(2), wrapResponse(1), wrapResponse(0));
        SharedQueue queue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(GetResponse response : responses) {
            queue.push(response);
        }
        Assertions.assertThat(queue.size()).isEqualTo(responses.size());
        for(int i = 0; i < responses.size(); ++i) {
            Assertions.assertThat(queue.pop()).isEqualTo(responses.get(i));
        }

        Assertions.assertThat(queue.size()).isEqualTo(0);
        Assertions.assertThat(queue.pop()).isNull();
    }

    @Test
    public void testPushNPopFromParallelQueue() throws InterruptedException {
        List<GetResponse> responses = List.of(wrapResponse(2), wrapResponse(1), wrapResponse(0));
        SharedQueue queue = new SharedQueue(ProcessType.PARALLEL);
        for(GetResponse response : responses) {
            queue.push(response);
        }
        Assertions.assertThat(queue.size()).isEqualTo(responses.size());
        for(int i = 0; i < responses.size(); ++i) {
            Assertions.assertThat(queue.pop()).isEqualTo(responses.get(i));
        }

        Assertions.assertThat(queue.size()).isEqualTo(0);
        Assertions.assertThat(queue.pop()).isNull();
    }

    private GetResponse wrapResponse(int i) {
        return new GetResponse(null, null, null, i);
    }

}
