package eu.kinae.k_rabbitmq_cdr.component.file;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FileSequentialTargetTest extends FileAbstractComponentTargetTest {

    @Test
    @Override
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        SharedQueue emptyQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        try(var target = mock(FileWriter.class);
            var component = new FileSequentialTarget(emptyQueue, target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(target, times(0)).push(any());
        }
    }

    @Test
    @Override
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(var message : MESSAGES)
            sharedQueue.push(message);

        try(var component = new FileSequentialTarget(sharedQueue, new FileWriter(tempDir))) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(var target = new FileReader(tempDir, KOptions.DEFAULT)) {
            assertThatSourceContainsAllMessagesUnsorted(target);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_sorted_messages() throws Exception {
        var sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(var message : MESSAGES)
            sharedQueue.push(message);

        try(var component = new FileSequentialTarget(sharedQueue, new FileWriter(tempDir))) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        var options = new KOptions(0, 1, true);
        try(var target = new FileReader(tempDir, options)) {
            assertThatSourceContainsAllMessagesSorted(target);
        }
    }
}