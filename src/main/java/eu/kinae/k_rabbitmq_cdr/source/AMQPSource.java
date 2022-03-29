package eu.kinae.k_rabbitmq_cdr.source;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPSource implements Source {

    private final String queue;
    private final ConnectionFactory factory;
    private final ObjectMapper om = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(AMQPSource.class);

    public AMQPSource(String uri, String queue) {
        logger.info("connection validation ...");
        this.queue = queue;
        this.factory = new ConnectionFactory();
        try {
            this.factory.setUri(uri);
            logger.info("connection validated");
        } catch (URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch (Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }

        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }


    @Override
    public boolean run() throws Exception {
        logger.info(""" 
                   starting connection on ...
                     Host : {}
                     Port : {}
                     Vhost : {}
                     Username : {}
                   """, factory.getHost(), factory.getPort(), factory.getVirtualHost(), factory.getUsername());
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            logger.info("connected and channel created");

            long current = System.currentTimeMillis();
            String prefix = current + "_" + queue + "_";
            logger.info("prefixing message with '{}'", prefix);

            logger.info("retrieving message from '{}' ...", queue);
            int count = getMessage(channel, prefix, 0);
            long end = System.currentTimeMillis();
            logger.info("message retrieved : {} in {}ms", count, (end - current));

            return count > 0;
        }

    }

    private int getMessage(Channel channel, String prefix, int count) throws IOException {
        GetResponse response = channel.basicGet(queue, false);
        if (response == null) {
            logger.info("no more message to get");
            return count;
        } else {
            if(count == 0) logger.info("estimate number of messages : {}", (response.getMessageCount() + 1));

            String filename = prefix + response.getEnvelope().getDeliveryTag();
            try(FileWriter fw = new FileWriter(filename)) {
                fw.write(new String(response.getBody()));
            }
            om.writeValue(new File(filename + "_props.json"), response.getProps());
        }

        return getMessage(channel, prefix, count + 1);
    }

}
