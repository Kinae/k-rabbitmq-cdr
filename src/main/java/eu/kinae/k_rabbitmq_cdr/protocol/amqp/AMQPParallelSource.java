package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import eu.kinae.k_rabbitmq_cdr.utils.SourceParams;

public class AMQPParallelSource extends AMQPComponentSource implements Runnable {

    public AMQPParallelSource(JCommanderParams params, SharedBuffer sharedBuffer, SharedStatus sharedStatus, SourceParams parameters) throws Exception {
        super(sharedBuffer, sharedStatus, parameters, params.sourceURI, params.sourceQueue);
    }

    @Override
    public void run() {
        start();
    }

}
