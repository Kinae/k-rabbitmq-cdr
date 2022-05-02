package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FileParallelSourceTest extends FileAbstractComponentSourceTest {

    @Override
    protected AbstractComponent getComponent(Source source, Target target, KOptions options) {
        return new FileParallelSource((FileReader) source, (SharedQueue) target, new SharedStatus(), options);
    }

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.PARALLEL);
    }

    @Test
    public void Start_source_in_single_thread_and_wait_at_most_60sec_to_consume_all_messages() throws Exception {
        var status = mock(SharedStatus.class);
        try(var target = getSharedQueue();
            var component = new FileParallelSource(getSource(), target, status, KOptions.DEFAULT)) {

            Future<?> future = Executors.newSingleThreadExecutor().submit(component);
            Awaitility.await().atMost(60, TimeUnit.SECONDS).until(future::isDone);

            assertThat(target.size()).isEqualTo(MESSAGES.size());
            assertThat(status.isConsumerAlive()).isFalse();
            verify(status, times(1)).notifySourceConsumerIsDone();
            assertThatSourceContainsAllMessagesUnsorted(target);
        }
    }
}
