package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

//@Testcontainers
public class AMQPComponentDirectLinkedTestNONO {

    //    private static final String SOURCE_Q = "source-q";
    //    private static final String TARGET_Q = "target-q";
    //
    //    @Container
    //    public final RabbitMQContainer rabbitmq = new RabbitMQContainer( DockerImageName.parse("rabbitmq:3-management"))
    //            .withQueue(SOURCE_Q)
    //            .withQueue(TARGET_Q);
    //
    //    @BeforeEach
    //    public void before() {
    ////        mockStatic(LoggerFactory.class);
    ////        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(mock(Logger.class));
    //    }
    //
    //    private String buildAMQPURI() {
    //        return String.format("amqp://%s:%s@%s:%d/%s", rabbitmq.getAdminUsername(), rabbitmq.getAdminPassword(), rabbitmq.getHost(), rabbitmq.getFirstMappedPort(), "%2F");
    //    }
    //
    //    @Test
    //    public void tte() throws Exception {
    //        String[] messages = new String[] {"TEST 1", "TEST 2", "TEST 3"};
    //        String amqpURI = buildAMQPURI();
    //        try(AMQPConnection sourceConnection = new AMQPConnection(amqpURI, SOURCE_Q)) {
    //            for(String message : messages) {
    //                sourceConnection.basicPublish(null, message.getBytes());
    //            }
    //        }
    //
    //        KParameters parameters = new KParameters(SupportedType.AMQP, amqpURI, SOURCE_Q, SupportedType.AMQP, amqpURI, TARGET_Q);
    //        try(AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(parameters, new KOptions(1))) {
    //            long actual = component.consumeNProduce();
    //            Assertions.assertThat(actual).isEqualTo(1);
    //        }
    //
    //        try(AMQPConnection targetConnection = new AMQPConnection(amqpURI, TARGET_Q)) {
    //            Assertions.assertThat(targetConnection.basicGet()).isNotNull();
    //            Assertions.assertThat(targetConnection.basicGet()).isNull();
    //        }
    //
    //    }
    //
    //    @Test
    //    public void test() throws Exception {
    //        String[] messages = new String[] {"TEST 1", "TEST 2", "TEST 3"};
    //        String amqpURI = buildAMQPURI();
    //        try(AMQPConnection sourceConnection = new AMQPConnection(amqpURI, SOURCE_Q)) {
    //            for(String message : messages) {
    //                sourceConnection.basicPublish(null, message.getBytes());
    //            }
    //        }
    //
    //        KParameters parameters = new KParameters(SupportedType.AMQP, amqpURI, SOURCE_Q, SupportedType.AMQP, amqpURI, TARGET_Q);
    //        try(AMQPComponentDirectLinked component = new AMQPComponentDirectLinked(parameters, new KOptions())) {
    //            Assertions.assertThat(component.consumeNProduce()).isEqualTo(messages.length);
    //        }
    //
    //        try(AMQPConnection targetConnection = new AMQPConnection(amqpURI, TARGET_Q)) {
    //            for(String message : messages) {
    //                Assertions.assertThat(targetConnection.basicGet().getBody()).isEqualTo(message.getBytes());
    //            }
    //        }
    //
    //
    //        //        AMQPConnection targetConnection = new AMQPConnection(String.format("amqp://%s:%s@%s:%d/%s", username, password, address, port, host), "to-queue");
    //
    ////        AMQPComponentDirectLinked amqpComponentDirectLinked = mock(AMQPComponentDirectLinked.class, CALLS_REAL_METHODS);
    ////        when(amqpComponentDirectLinked.getSource()).thenReturn(sourceConnection);
    ////        when(amqpComponentDirectLinked.getTarget()).thenReturn(targetConnection);
    ////        when(amqpComponentDirectLinked.getOptions()).thenReturn(new KOptions());
    ////        when(amqpComponentDirectLinked.logger.).thenReturn(LoggerFactory.getLogger(getClass()));
    //
    ////        Awaitility.await().atMost(100, TimeUnit.SECONDS).untilAsserted(() -> Assertions.assertThat(component.consumeNProduce()).isEqualTo(messages.length));
    //
    //        //        amqpComponentDirectLinked.start();
    //
    ////        Mockito.verify(amqpComponentDirectLinked, Mockito.times(1)).getSource().basicGet();
    ////        Mockito.verify(amqpComponentDirectLinked, Mockito.times(1)).close();
    ////        Mockito.verify(amqpComponentDirectLinked, Mockito.times(1)).onFinally();
    //
    //    }

}
