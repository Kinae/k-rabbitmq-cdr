package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialSource extends AMQPComponentSource {

    public AMQPSequentialSource(AMQPConnection connection, SharedQueue sharedQueue) {
        this(connection, sharedQueue, KOptions.DEFAULT);
    }

    public AMQPSequentialSource(AMQPConnection connection, SharedQueue sharedQueue, KOptions options) {
        super(connection, sharedQueue, options);
    }

}
