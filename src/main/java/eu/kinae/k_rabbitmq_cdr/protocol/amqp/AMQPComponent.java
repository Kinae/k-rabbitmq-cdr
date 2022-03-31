package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.net.URISyntaxException;

import com.rabbitmq.client.ConnectionFactory;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AMQPComponent implements Component {

    protected final ConnectionFactory factory;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected AMQPComponent(String uri) {
        this.factory = new ConnectionFactory();
        try {
            this.factory.setUri(uri);
            logger.info("connection validated");
        } catch(URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }
}
