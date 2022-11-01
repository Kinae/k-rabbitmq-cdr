package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPConnectionWriter implements Target {

    private final Channel channel;
    private final String queue;
    private final SharedStatus sharedStatus;
    private final Logger logger = LoggerFactory.getLogger(AMQPConnectionWriter.class);

    public AMQPConnectionWriter(AMQPConnection connection, String queue) {
        this(connection, queue, null);
    }

    public AMQPConnectionWriter(AMQPConnection connection, String queue, SharedStatus sharedStatus) {
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
    public void push(KMessage message, KOptions options) throws IOException {
        channel.basicPublish("", queue, false, false, message.properties(), message.body());
        if(sharedStatus != null) {
            sharedStatus.incrementWrite();
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if(channel.isOpen()) {
                channel.close();
            }
        } catch(Exception e) {
            logger.warn("Cannot close channel");
        }
    }
}
