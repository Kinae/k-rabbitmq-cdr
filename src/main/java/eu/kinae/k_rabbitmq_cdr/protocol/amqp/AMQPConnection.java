package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.IOException;
import java.net.URISyntaxException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPConnection implements AutoCloseable, Source, Target {

    private final Connection connection;
    private final Channel channel;
    private final String queue;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AMQPConnection(String uri, String queue) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(uri);
            logger.info("connection validated");
        } catch(URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }

        this.queue = queue;
        logger.info("starting connection on {}", factory);
        connection = factory.newConnection();
        logger.info("connection successful {}", connection);
        logger.info("creating channel");
        channel = connection.createChannel();
        logger.info("channel created {} ", channel);
    }

    public void close() {
        try {
            logger.info("closing channel if open");
            if(channel.isOpen()) {
                logger.info("channel closed");
                channel.close();
            }
        } catch(Exception e) {
            logger.warn("Cannot close channel", e);
        }

        try {
            logger.info("closing connection if open");
            if(connection.isOpen()) {
                logger.info("connection closed");
                connection.close();
            }
        } catch(Exception e) {
            logger.warn("Cannot close connection");
        }
    }

    @Override
    public KMessage pop() throws IOException {
        GetResponse response = channel.basicGet(queue, false);
        if(response == null)
            return null;
        return new KMessage(response.getProps(), response.getBody(), response.getMessageCount());
    }

    @Override
    public void push(KMessage message) throws IOException {
        channel.basicPublish("", queue, false, false, message.properties(), message.body());
    }
}
