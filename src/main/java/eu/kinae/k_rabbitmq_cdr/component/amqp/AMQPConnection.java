package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.io.IOException;
import java.net.URISyntaxException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPConnection implements AutoCloseable {

    private final Connection connection;
    private final Logger logger = LoggerFactory.getLogger(AMQPConnection.class);

    public AMQPConnection(String uri) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);
            logger.info("creating AMQP connection on {}", buildSafeURI(factory));
            connection = factory.newConnection();
        } catch(URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    public long countMessages(String queue) {
        try (Channel channel = createChannel()) {
            return channel.messageCount(queue);
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    public Channel createChannel() throws IOException {
        return connection.createChannel();
    }

    private String buildSafeURI(ConnectionFactory factory) {
        return String.format("[host=%s, port=%s, vhost=%s]", factory.getHost(), factory.getPort(), factory.getVirtualHost());
    }

    @Override
    public void close() {
        try {
            if(connection.isOpen()) {
                connection.close();
            }
        } catch(Exception e) {
            logger.warn("Cannot close connection", e);
        }
    }
}
