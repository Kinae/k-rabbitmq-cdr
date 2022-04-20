package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.concurrent.Callable;

import com.rabbitmq.client.Channel;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AMQPComponentTarget implements Callable<Long> {

    private Channel channel;

    public AMQPParallelTarget(AMQPConnection connection, SharedQueue sharedQueue, SharedStatus sharedStatus) {
        super(connection, sharedQueue, sharedStatus);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return !sharedStatus.isConsumerAlive();
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
