package eu.kinae.k_rabbitmq_cdr.component.amqp;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialTarget extends AbstractComponentTarget {

    public AMQPSequentialTarget(SharedQueue source, AMQPConnectionWriter target, KOptions options) {
        super(source, target, options);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }
}
