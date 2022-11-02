package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPConnectionReader implements Source {

    private final static Logger logger = LoggerFactory.getLogger(AMQPConnectionReader.class);

    private final Channel channel;
    private final String queue;
    private final SharedStatus sharedStatus;

    public AMQPConnectionReader(AMQPConnection connection, String queue) {
        this(connection, queue, null);
    }

    public AMQPConnectionReader(AMQPConnection connection, String queue, SharedStatus sharedStatus) {
        this.queue = queue;
        this.sharedStatus = sharedStatus;
        try {
            this.channel = connection.createChannel();
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    @Override
    public KMessage pop(KOptions options) throws IOException {
        GetResponse response = channel.basicGet(queue, false);
        if(response == null) {
            return null;
        }
        if(sharedStatus != null) {
            sharedStatus.incrementRead();
        }
        AMQP.BasicProperties props = options.bodyOnly() ? null : response.getProps();
        return new KMessage(props, response.getBody(), response.getEnvelope().getDeliveryTag());
    }

    @Override
    public void close() throws Exception {
        try {
            if(channel.isOpen()) {
                channel.close();
            }
        } catch(Exception e) {
            logger.warn("Cannot close channel", e);
        }
    }
}
