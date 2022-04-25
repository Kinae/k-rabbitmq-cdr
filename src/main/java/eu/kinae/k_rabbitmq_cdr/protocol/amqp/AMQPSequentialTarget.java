package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialTarget extends AbstractComponentTarget implements AMQPComponent {

    public AMQPSequentialTarget(SharedQueue source, AMQPConnection target) {
        super(source, target);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }
}
