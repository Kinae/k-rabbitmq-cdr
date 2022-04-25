package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelSource extends AMQPComponentSource implements Callable<Long> {

    public AMQPParallelSource(AMQPConnection source, SharedQueue target, SharedStatus sharedStatus, KOptions options) {
        super(source, target, sharedStatus, options);
    }

    @Override
    public Long call() {
        return start();
    }
}
