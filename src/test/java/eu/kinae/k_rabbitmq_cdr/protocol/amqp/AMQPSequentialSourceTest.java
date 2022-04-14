package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

//import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
//import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//
public class AMQPSequentialSourceTest {
    //
    //    private SharedQueue sharedQueue;
    //
    //    @BeforeEach
    //    public void before() throws Exception {
    //        super.before();
    //        sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
    //    }
    //
    //    @Test
    //    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
    //        String amqpURI = buildAMQPURI();
    //        try(AMQPSequentialSource sequentialSource = new AMQPSequentialSource(buildKParameters(amqpURI, SOURCE_Q), sharedQueue, KOptions.DEFAULT)) {
    //            long actual = sequentialSource.consumeNProduce();
    //            Assertions.assertThat(actual).isEqualTo(MESSAGES.size());
    //        }
    //
    //        Assertions.assertThat(sharedQueue.size()).isEqualTo(MESSAGES.size());
    //        for(KMessage message : MESSAGES) {
    //            KMessage actual = sharedQueue.pop();
    //            Assertions.assertThat(actual.body()).isEqualTo(message.body());
    //        }
    //    }
    //
    //    @Override
    //    protected AMQPComponent getComponent(String queue, KOptions options) throws Exception {
    //        return new AMQPSequentialSource(buildKParameters(buildAMQPURI(), queue), sharedQueue, options);
    //    }

    //    private KParameters buildKParameters(String uri, String queue) {
    //        return new KParameters(SupportedType.AMQP, uri, queue, null, null, null);
    //    }

}
