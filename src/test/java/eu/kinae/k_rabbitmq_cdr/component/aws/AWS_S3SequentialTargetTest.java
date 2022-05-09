package eu.kinae.k_rabbitmq_cdr.component.aws;

import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AWS_S3SequentialTargetTest extends AWS_S3AbstractComponentTargetTest {

    @Test
    @Override
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var emptyQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        try(var target = mock(AWS_S3Writer.class);
            var component = new AWS_S3SequentialTarget(emptyQueue, target)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(target, times(0)).push(any());
        }
    }

    @Test
    @Override
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(var message : MESSAGES)
            sharedQueue.push(message);

        try(var component = new AWS_S3SequentialTarget(sharedQueue, new AWS_S3Writer(s3, bucket, PREFIX))) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(var target = new AWS_S3Reader(s3, bucket, PREFIX, KOptions.DEFAULT)) {
            assertThatSourceContainsAllMessagesUnsorted(target);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_sorted_messages() throws Exception {
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        for(var message : MESSAGES)
            sharedQueue.push(message);

        try(var component = new AWS_S3SequentialTarget(sharedQueue, new AWS_S3Writer(s3, bucket, PREFIX))) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        var options = new KOptions(0, 1, true);
        try(var target = new AWS_S3Reader(s3, bucket, PREFIX, options)) {
            assertThatSourceContainsAllMessagesSorted(target);
        }
    }

}
