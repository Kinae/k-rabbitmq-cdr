package eu.kinae.k_rabbitmq_cdr.from;


import java.io.IOException;
import java.net.URISyntaxException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FromAMQP implements Source {

    private final String queue;
    private final ConnectionFactory factory;
    private final Logger logger = LoggerFactory.getLogger(FromAMQP.class);

    public FromAMQP(String uri, String queue) {
        logger.info("connection setup ...");
        this.queue = queue;
        this.factory = new ConnectionFactory();
        try {
            this.factory.setUri(uri);
            logger.info("connection setup ok");
        } catch (URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch (Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
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
            logger.info("connection and channel ok");
            logger.info("retrieving message from '{}' ...", queue);
            int count = getMessage(channel, 0);
            logger.info("message retrieved : {}", count);
            return count > 0;
        }

    }

    private int getMessage(Channel channel, int count) throws IOException, InterruptedException {
        GetResponse response = channel.basicGet(queue, false);
        if (response == null) {
            logger.info("no more message to get");
            return count;
        } else {
            AMQP.BasicProperties props = response.getProps();
            byte[] body = response.getBody();
            long deliveryTag = response.getEnvelope().getDeliveryTag();
            logger.info("Props: " + props);
            logger.info("Body: " + new String(body));
            logger.info("deliveryTag: " + deliveryTag);
        }

        return getMessage(channel, count + 1);
    }

}
