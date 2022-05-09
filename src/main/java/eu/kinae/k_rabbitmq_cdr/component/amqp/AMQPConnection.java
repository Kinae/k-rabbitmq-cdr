package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.io.IOException;
import java.net.URISyntaxException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPConnection implements AutoCloseable, Source, Target {

    private final Connection connection;
    private final String queue;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Channel channel;

    public AMQPConnection(String uri, String queue) {
        logger.info("creating AMQP connection on {} targeting queue {}", uri, queue);
        this.queue = queue;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);
            connection = factory.newConnection();
        } catch(URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }

    }

    Channel createChannel() throws IOException {
        logger.info("creating channel");
        return connection.createChannel();
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

    @Override
    public KMessage pop() throws IOException {
        if(channel == null)
            channel = createChannel();
        GetResponse response = channel.basicGet(queue, false);
        if(response == null)
            return null;
        return new KMessage(response.getProps(), response.getBody(), response.getMessageCount(), response.getEnvelope().getDeliveryTag());
    }

    @Override
    public void push(KMessage message) throws IOException {
        if(channel == null)
            channel = createChannel();
        channel.basicPublish("", queue, false, false, message.properties(), message.body());
    }

    protected void push(KMessage message, Channel channel) throws IOException {
        channel.basicPublish("", queue, false, false, message.properties(), message.body());
    }

}
