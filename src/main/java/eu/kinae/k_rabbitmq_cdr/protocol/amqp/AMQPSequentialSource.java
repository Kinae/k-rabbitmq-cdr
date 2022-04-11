package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialSource extends AMQPComponentSource {

    public AMQPSequentialSource(JCommanderParams params, SharedQueue sharedQueue, KOptions options) throws Exception {
        super(params.sourceURI, params.sourceQueue, sharedQueue, options);
    }

}
