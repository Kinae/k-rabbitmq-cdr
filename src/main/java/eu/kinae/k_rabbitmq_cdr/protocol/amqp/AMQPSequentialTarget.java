package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;

public class AMQPSequentialTarget extends AMQPComponentTarget {

    public AMQPSequentialTarget(JCommanderParams params, SharedBuffer sharedBuffer) throws Exception {
        super(sharedBuffer, params.targetURI, params.targetQueue);
    }

}
