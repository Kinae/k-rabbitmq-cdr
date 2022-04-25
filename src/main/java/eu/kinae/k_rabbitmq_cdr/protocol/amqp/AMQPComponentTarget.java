package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

abstract class AMQPComponentTarget extends AMQPComponent {

    protected AMQPComponentTarget(SharedQueue source, AMQPConnection target) {
        super(source, target);
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
