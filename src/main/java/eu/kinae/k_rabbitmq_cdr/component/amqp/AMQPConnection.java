package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.net.URISyntaxException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AMQPConnection implements AutoCloseable {

    protected final Connection connection;
    protected Channel channel;
    protected final String queue;
    protected final SharedStatus sharedStatus;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AMQPConnection(String uri, String queue) {
        this(uri, queue, null);
    }

    public AMQPConnection(String uri, String queue, SharedStatus sharedStatus) {
        this.queue = queue;
        this.sharedStatus = sharedStatus;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);
            logger.info("creating AMQP connection on {} targeting queue {}", buildSafeURI(factory), queue);
            connection = factory.newConnection();
        } catch(URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    private String buildSafeURI(ConnectionFactory factory) {
        return String.format("[host=%s, port=%s, vhost=%s]", factory.getHost(), factory.getPort(), factory.getVirtualHost());
    }

    public void close() {
        try {
            if(channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch(Exception e) {
            logger.warn("Cannot close channel", e);
        }

        try {
            if(connection.isOpen()) {
                connection.close();
            }
        } catch(Exception e) {
            logger.warn("Cannot close connection");
        }
    }
}
