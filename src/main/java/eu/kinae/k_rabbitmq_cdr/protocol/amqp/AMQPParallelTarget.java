package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AMQPComponentTarget implements Runnable {

    public AMQPParallelTarget(AMQPConnection connection, SharedQueue sharedQueue, SharedStatus sharedStatus) {
        super(connection, sharedQueue, sharedStatus);
    }

    @Override
    public void run() {
        start();
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return !sharedStatus.isConsumerAlive();
    }
}
