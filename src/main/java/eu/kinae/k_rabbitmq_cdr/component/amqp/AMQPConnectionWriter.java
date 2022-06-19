package eu.kinae.k_rabbitmq_cdr.component.amqp;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPConnectionWriter extends AMQPConnection implements Target {

    public AMQPConnectionWriter(String uri, String queue) {
        super(uri, queue);
    }

    public AMQPConnectionWriter(String uri, String queue, SharedStatus sharedStatus) {
        super(uri, queue, sharedStatus);
    }

    Channel createChannel() throws IOException {
        return connection.createChannel();
    }

    @Override
    public void push(KMessage message, KOptions options) throws IOException {
        if(channel == null) {
            channel = createChannel();
        }
        if(sharedStatus != null) {
            sharedStatus.incrementWrite();
        }
        channel.basicPublish("", queue, false, false, message.properties(), message.body());
    }

    protected void push(KMessage message, Channel channel, KOptions options) throws IOException {
        if(sharedStatus != null) {
            sharedStatus.incrementWrite();
        }
        channel.basicPublish("", queue, false, false, message.properties(), message.body());
    }

}
