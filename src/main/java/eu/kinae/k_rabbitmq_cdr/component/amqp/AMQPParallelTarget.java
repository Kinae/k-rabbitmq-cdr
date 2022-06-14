package eu.kinae.k_rabbitmq_cdr.component.amqp;

import com.rabbitmq.client.Channel;
import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentParallelTarget;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AbstractComponentParallelTarget {

    private Channel channel;

    public AMQPParallelTarget(SharedQueue source, AMQPConnectionWriter target, SharedStatus sharedStatus) {
        super(source, target, sharedStatus);
    }

    @Override
    public Long call() throws Exception {
        try(var channel = ((AMQPConnectionWriter) target).createChannel()) {
            this.channel = channel;
            return start();
        }
    }

    @Override
    public void push(KMessage message) throws Exception {
        if(target instanceof AMQPConnectionWriter connection) {
            connection.push(message, channel);
        } else {
            super.push(message);
        }
    }

}
