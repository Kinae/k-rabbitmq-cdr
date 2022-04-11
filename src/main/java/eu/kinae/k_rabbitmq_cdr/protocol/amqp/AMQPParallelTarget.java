package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AMQPComponentTarget implements Runnable {

    public AMQPParallelTarget(JCommanderParams params, SharedQueue sharedQueue, SharedStatus sharedStatus) throws Exception {
        super(params.targetURI, params.targetQueue, sharedQueue, sharedStatus);
    }

    @Override
    public void run() {
        start();
    }

    @Override
    protected boolean breakIfResponseIsNull() {
        return !sharedStatus.isConsumerAlive();
    }
}
