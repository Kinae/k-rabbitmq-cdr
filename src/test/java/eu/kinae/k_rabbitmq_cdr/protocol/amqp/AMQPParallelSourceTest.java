package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

public class AMQPParallelSourceTest {

    //    private SharedQueue sharedQueue;
    //    private SharedStatus sharedStatus;
    //
    //    @BeforeEach
    //    public void before() throws Exception {
    //        super.before();
    //        sharedQueue = new SharedQueue(ProcessType.PARALLEL);
    //        sharedStatus = new SharedStatus();
    //    }
    //
    //    @Test
    //    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
    //        String amqpURI = buildAMQPURI();
    //        try(AMQPParallelSource sequentialSource = new AMQPParallelSource(buildKParameters(amqpURI, SOURCE_Q), sharedQueue, sharedStatus, KOptions.DEFAULT)) {
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
    //    @Test
    //    public void Start_source_in_single_thread_and_wait_at_most_60sec_to_consume_all_messages() throws Exception {
    //        String amqpURI = buildAMQPURI();
    //        try(AMQPParallelSource sequentialSource = new AMQPParallelSource(buildKParameters(amqpURI, SOURCE_Q), sharedQueue, sharedStatus, KOptions.DEFAULT)) {
    //            Future<?> future = Executors.newSingleThreadExecutor().submit(sequentialSource);
    //            Awaitility.await().atMost(60, TimeUnit.SECONDS).until(future::isDone);
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
    //        return new AMQPParallelSource(buildKParameters(buildAMQPURI(), queue), sharedQueue, sharedStatus, options);
    //    }
    //
    //    private KParameters buildKParameters(String uri, String queue) {
    //        return new KParameters(SupportedType.AMQP, uri, queue, null, null, null);
    //    }

}
