package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelSource extends AMQPComponentSource implements Runnable {

    public AMQPParallelSource(JCommanderParams params, SharedQueue sharedQueue, SharedStatus sharedStatus, KOptions options) throws Exception {
        super(params.sourceURI, params.sourceQueue, sharedQueue, sharedStatus, options);
    }

    @Override
    public void run() {
        start();
    }

}
