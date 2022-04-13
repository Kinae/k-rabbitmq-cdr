package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelSource extends AMQPComponentSource implements Runnable {

    public AMQPParallelSource(KParameters parameters, SharedQueue sharedQueue, SharedStatus sharedStatus, KOptions options) throws Exception {
        super(parameters.sourceURI(), parameters.sourceQueue(), sharedQueue, sharedStatus, options);
    }

    @Override
    public void run() {
        start();
    }

}
