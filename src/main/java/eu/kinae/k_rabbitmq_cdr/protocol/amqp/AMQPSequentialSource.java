package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialSource extends AMQPComponentSource {

    public AMQPSequentialSource(KParameters parameters, SharedQueue sharedQueue, KOptions options) throws Exception {
        super(parameters.sourceURI(), parameters.sourceQueue(), sharedQueue, options);
    }

}
