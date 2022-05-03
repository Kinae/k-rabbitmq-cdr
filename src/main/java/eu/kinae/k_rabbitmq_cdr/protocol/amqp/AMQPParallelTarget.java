package eu.kinae.k_rabbitmq_cdr.protocol.amqp;


import com.rabbitmq.client.Channel;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentParallelTarget;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AbstractComponentParallelTarget {

    private Channel channel;

    public AMQPParallelTarget(SharedQueue source, AMQPConnection target, SharedStatus sharedStatus) {
        super(source, target, sharedStatus);
    }

    @Override
    public Long call() throws Exception {
        try(var channel = ((AMQPConnection) target).createChannel()) {
            this.channel = channel;
            return start();
        }
    }

    @Override
    public void push(KMessage message) throws Exception {
        if(target instanceof AMQPConnection connection) {
            connection.push(message, channel);
        } else {
            super.push(message);
        }
    }

}
