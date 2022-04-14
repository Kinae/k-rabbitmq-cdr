package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialTarget extends AMQPComponentTarget {

    public AMQPSequentialTarget(AMQPConnection connection, SharedQueue sharedQueue) {
        super(connection, sharedQueue);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }
}
