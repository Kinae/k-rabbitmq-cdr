package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SourceParams;

public class AMQPSequentialSource extends AMQPComponentSource {

    public AMQPSequentialSource(JCommanderParams params, SharedBuffer sharedBuffer, SourceParams parameters) throws Exception {
        super(sharedBuffer, parameters, params.sourceURI, params.sourceQueue);
    }

}
