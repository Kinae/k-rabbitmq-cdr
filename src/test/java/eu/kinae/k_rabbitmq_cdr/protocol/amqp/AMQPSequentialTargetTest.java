package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

//@Testcontainers
public class AMQPSequentialTargetTest {

    //    public static final String TARGET_Q = "target-q";
    //    public static final List<KMessage> MESSAGES = Stream.of(new Integer[]{0, 1, 2, 3, 4}).map(it -> new KMessage("TEST_" + it)).collect(Collectors.toList());
    //
    //    @Container
    //    public final RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
    //            .withQueue(TARGET_Q);
    //
    //
    //    private String buildAMQPURI() {
    //        return String.format("amqp://%s:%s@%s:%d/%s", rabbitmq.getAdminUsername(), rabbitmq.getAdminPassword(), rabbitmq.getHost(), rabbitmq.getFirstMappedPort(), "%2F");
    //    }
    //
    //    @Test
    //    public void Consume_from_empty_queue_produce_nothing() throws Exception {
    //        String amqpURI = buildAMQPURI();
    //        SharedQueue emptyQueue = new SharedQueue(ProcessType.SEQUENTIAL);
    //
    //        try(AMQPSequentialTarget sequentialTarget = new AMQPSequentialTarget(buildKParameters(amqpURI), emptyQueue)) {
    //            long actual = sequentialTarget.consumeNProduce();
    //            Assertions.assertThat(actual).isEqualTo(emptyQueue.size());
    //        }
    //
    //        try(AMQPConnection targetConnection = new AMQPConnection(amqpURI, TARGET_Q)) {
    //            Assertions.assertThat(targetConnection.pop()).isNull();
    //        }
    //    }
    //
    //    @Test
    //    public void Produced_messages_are_equal_to_consumed_messages() throws Exception {
    //        String amqpURI = buildAMQPURI();
    //        SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
    //        for(KMessage message : MESSAGES)
    //            sharedQueue.push(message);
    //
    //        try(AMQPSequentialTarget sequentialTarget = new AMQPSequentialTarget(buildKParameters(amqpURI), sharedQueue)) {
    //            long actual = sequentialTarget.consumeNProduce();
    //            Assertions.assertThat(actual).isEqualTo(MESSAGES.size());
    //        }
    //
    //        Assertions.assertThat(sharedQueue.size()).isEqualTo(0);
    //        try(AMQPConnection targetConnection = new AMQPConnection(amqpURI, TARGET_Q)) {
    //            for(KMessage message : MESSAGES) {
    //                Assertions.assertThat(targetConnection.pop().body()).isEqualTo(message.body());
    //            }
    //        }
    //    }
    //
    //
    //    private KParameters buildKParameters(String uri) {
    //        return new KParameters(null, null, null, SupportedType.AMQP, uri, TARGET_Q);
    //    }

}
