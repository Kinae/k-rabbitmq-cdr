package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialTarget extends AMQPComponentTarget {

    public AMQPSequentialTarget(JCommanderParams params, SharedQueue sharedQueue) throws Exception {
        super(params.targetURI, params.targetQueue, sharedQueue);
    }

    @Override
    protected boolean breakIfResponseIsNull() {
        return true;
    }
}
