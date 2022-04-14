package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

abstract class AMQPComponentTarget extends AMQPComponent {

    protected final SharedStatus sharedStatus;

    protected AMQPComponentTarget(AMQPConnection connection, SharedQueue sharedQueue) {
        this(connection, sharedQueue, null);
    }

    protected AMQPComponentTarget(AMQPConnection connection, SharedQueue sharedQueue, SharedStatus sharedStatus) {
        super(sharedQueue, connection);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected void onFinally() {

    }

    @Override
    public long consumeNProduce() throws Exception {
        long count = 0;
        do {
            KMessage message = pop();
            if(message == null) {
                logger.debug("Waiting for message ...");
                if(stopConsumingIfResponseIsNull())
                    break;
            } else {
                count++;
                push(message);
            }
        } while(true);
        return count;
    }

    protected abstract boolean stopConsumingIfResponseIsNull();

}
