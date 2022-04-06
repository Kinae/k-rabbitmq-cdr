package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.net.URISyntaxException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AMQPComponent implements Component {

    protected final Connection connection;
    protected final Channel channel;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected AMQPComponent(String uri) throws Exception {
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

        logger.info("starting connection on {} ...", factory);
        connection = factory.newConnection();
        logger.info("connection successful {}", connection);
        logger.info("creating channel ...");
        channel = connection.createChannel();
        logger.info("channel created {} ", channel);
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    protected void close() {
        try {
            if(channel.isOpen())
                channel.close();
        } catch(Exception e) {
            logger.warn("Cannot close channel", e);
        }

        try {
            if(connection.isOpen())
                connection.close();
        } catch(Exception e) {
            logger.warn("Cannot close connection");
        }
    }
}
