package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelSource extends AMQPComponentSource implements Callable<Long> {

    public AMQPParallelSource(AMQPConnection connection, SharedQueue sharedQueue, SharedStatus sharedStatus, KOptions options) {
        super(connection, sharedQueue, sharedStatus, options);
    }

    @Override
    public Long call() {
        return start();
    }
}
