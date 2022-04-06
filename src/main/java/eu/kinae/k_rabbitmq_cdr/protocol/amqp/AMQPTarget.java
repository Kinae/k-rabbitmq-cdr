package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

public class AMQPTarget {

    //    private final BlockingQueue<GetResponse> bq = null;
    //
    //    public AMQPTarget(String uri, String queue) throws Exception {
    //        super(uri, queue);
    //    }
    //
    ////    @Override
    //    public boolean publish() throws Exception {
    //        logger.info("""
    //                            starting connection on ...
    //                              Host : {}
    //                              Port : {}
    //                              Vhost : {}
    //                              Username : {}
    //                            """, factory.getHost(), factory.getPort(), factory.getVirtualHost(), factory.getUsername());
    //        try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
    //            logger.info("connected and channel created");
    //
    //            logger.info("listing files ...");
    //            Pattern p = Pattern.compile(".*[^.json]$");
    //            File[] files = Constant.PROJECT_TMPDIR.toFile().listFiles(it -> p.matcher(it.getName()).matches());
    //            if(files == null) {
    //                logger.warn("no files were listed !");
    //                return false;
    //            }
    //            logger.info("number of files listed : {}", files.length);
    //            for(File file : files) {
    //                byte[] body = Files.readAllBytes(file.toPath());
    //                AMQP.BasicProperties props = CustomObjectMapper.om.readValue(new File(file.getPath() + Constant.FILE_PROPERTIES_SUFFIX), AMQP.BasicProperties.class);
    //                channel.basicPublish("", queue, false, false, props, body);
    //            }
    //        }
    //
    //        return false;
    //    }
    //
    //    public boolean run2(List<GetResponse> list) throws Exception {
    //        logger.info("""
    //                            starting connection on ...
    //                              Host : {}
    //                              Port : {}
    //                              Vhost : {}
    //                              Username : {}
    //                            """, factory.getHost(), factory.getPort(), factory.getVirtualHost(), factory.getUsername());
    //        try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
    //            logger.info("connected and channel created");
    //
    //            if(list.isEmpty()) {
    //                logger.warn("nothing to publish !");
    //                return false;
    //            }
    //
    //            logger.info("number of messages to publish : {}", list.size());
    //            long start = System.currentTimeMillis();
    //            long count = publish(channel, list);
    //            long end = System.currentTimeMillis();
    //            logger.info("messages published : {} in {}ms", count, (end - start));
    //
    //        }
    //
    //        return false;
    //    }
    //
    //
    //    @Override
    //    public void run() {
    //        produce(bq);
    //    }
    //
    //
    //
    //    private void consume(GetResponse take) {
    //
    //    }

    // SOURCE
    //
    //    @Override
    //    public boolean consume() throws Exception {
    //        String prefix = queue + "_";
    //        logger.debug("prefixing message with '{}'", prefix);
    //
    //        logger.info("retrieving message from '{}' ...", queue);
    //        long start = System.currentTimeMillis();
    //        int count = getMessage(prefix, 0);
    //        long end = System.currentTimeMillis();
    //        logger.info("message retrieved : {} in {}ms", count, (end - start));
    //
    //        return count > 0;
    //    }

    //    private int getMessage(String prefix, int count) throws IOException {
    //        GetResponse response = channel.basicGet(queue, false);
    //        if(response == null) {
    //            logger.debug("no more message to get");
    //            return count;
    //        } else {
    //            if(count == 0)
    //                logger.info("estimate number of messages : {}", (response.getMessageCount() + 1));
    //
    //            String filename = prefix + response.getEnvelope().getDeliveryTag();
    //            Path path = Files.createFile(Path.of(Constant.PROJECT_TMPDIR.toString(), filename));
    //            path.toFile().deleteOnExit();
    //            Files.writeString(path, new String(response.getBody()), StandardOpenOption.TRUNCATE_EXISTING);
    //
    //            path = Files.createFile(Path.of(Constant.PROJECT_TMPDIR.toString(), filename + Constant.FILE_PROPERTIES_SUFFIX));
    //            path.toFile().deleteOnExit();
    //            CustomObjectMapper.om.writeValue(path.toFile(), response.getProps());
    //        }
    //
    //        return getMessage(prefix, count + 1);
    //    }

}
