package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AMQPComponentTarget implements Runnable {

    public AMQPParallelTarget(KParameters parameters, SharedQueue sharedQueue, SharedStatus sharedStatus) throws Exception {
        super(parameters.targetURI(), parameters.targetQueue(), sharedQueue, sharedStatus);
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
