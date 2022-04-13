package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialTarget extends AMQPComponentTarget {

    public AMQPSequentialTarget(KParameters parameters, SharedQueue sharedQueue) throws Exception {
        super(parameters.targetURI(), parameters.targetQueue(), sharedQueue);
    }

    @Override
    protected boolean breakIfResponseIsNull() {
        return true;
    }
}
