package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPToAMQPConnector implements Connector {

    private static final Logger logger = LoggerFactory.getLogger(AMQPToAMQPConnector.class);

    public AMQPToAMQPConnector() {
    }

    @Override
    public void run(JCommanderParams params) {
        AMQPSource source = new AMQPSource(params.sourceURI, params.sourceQueue);
        AMQPTarget target = new AMQPTarget(params.targetURI, params.targetQueue);
        try {
            source.run();
            target.run();
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }
}
