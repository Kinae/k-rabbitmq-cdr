package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AMQPComponentTarget implements Runnable {

    public AMQPParallelTarget(JCommanderParams params, SharedBuffer sharedBuffer, SharedStatus sharedStatus) throws Exception {
        super(sharedBuffer, sharedStatus, params.targetURI, params.targetQueue);
    }

    @Override
    public void run() {
        start();
    }
}
