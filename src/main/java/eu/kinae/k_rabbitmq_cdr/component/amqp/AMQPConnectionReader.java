package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPConnectionReader extends AMQPConnection implements Source {

    public AMQPConnectionReader(String uri, String queue) {
        super(uri, queue);
    }

    public AMQPConnectionReader(String uri, String queue, SharedStatus sharedStatus) {
        super(uri, queue, sharedStatus);
        try {
            if(sharedStatus != null) {
                Channel channel = connection.createChannel();
                sharedStatus.setTotal(channel.messageCount(queue));
                channel.close();
            }
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    Channel createChannel() throws IOException {
        return connection.createChannel();
    }

    @Override
    public KMessage pop(KOptions options) throws IOException {
        if(channel == null) {
            channel = createChannel();
        }
        GetResponse response = channel.basicGet(queue, false);
        if(response == null) {
            return null;
        }
        if(sharedStatus != null) {
            sharedStatus.incrementRead();
        }
        AMQP.BasicProperties props = options.bodyOnly() ? null : response.getProps();
        return new KMessage(props, response.getBody(), response.getMessageCount(), response.getEnvelope().getDeliveryTag());
    }

}
