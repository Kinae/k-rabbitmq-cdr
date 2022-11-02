package eu.kinae.k_rabbitmq_cdr.component.aws;

import java.util.UUID;

import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AWS_S3SequentialTargetTest extends AWS_S3AbstractComponentTargetTest {

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.SEQUENTIAL);
    }

    @Test
    @Override
    public void Consume_from_empty_queue_produce_nothing() throws Exception {
        var options = KOptions.DEFAULT;
        var emptyQueue = getSharedQueue();
        try(var target = mock(AWS_S3Writer.class);
            var component = new SequentialComponentTarget(emptyQueue, target, options)) {

            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(0);
            verify(target, times(0)).push(any());
        }
    }

    @Test
    @Override
    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
        var options = KOptions.DEFAULT;
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var sharedQueue = getSharedQueue();
        for(var message : MESSAGES) {
            sharedQueue.push(message);
        }

        try(var component = new SequentialComponentTarget(sharedQueue, new AWS_S3Writer(s3, bucket, PREFIX, mock(SharedStatus.class)), options)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(var target = new AWS_S3Reader(new AWS_S3ReaderInfo(s3, bucket, PREFIX, options), mock(SharedStatus.class))) {
            assertThatContainsAllMessages(target, options);
        }
    }

    @Test
    public void Produced_messages_are_equal_to_consumed_sorted_messages() throws Exception {
        var options = KOptions.SORTED;
        var bucket = UUID.randomUUID().toString();
        s3.createBucket(it -> it.bucket(bucket));

        var sharedQueue = getSharedQueue();
        for(var message : MESSAGES) {
            sharedQueue.push(message);
        }

        try(var component = new SequentialComponentTarget(sharedQueue, new AWS_S3Writer(s3, bucket, PREFIX), options)) {
            long actual = component.consumeNProduce();

            assertThat(actual).isEqualTo(MESSAGES.size());
        }

        assertThat(sharedQueue.size()).isEqualTo(0);
        try(var target = new AWS_S3Reader(new AWS_S3ReaderInfo(s3, bucket, PREFIX, options))) {
            assertThatContainsAllMessages(target, options);
        }
    }
}
