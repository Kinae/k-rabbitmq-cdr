package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelSource extends AbstractComponentSource implements Callable<Long>, AMQPComponent {

    private final SharedStatus sharedStatus;

    public AMQPParallelSource(AMQPConnection source, SharedQueue target, SharedStatus sharedStatus, KOptions options) {
        super(source, target, options);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected void onFinally() {
        if(sharedStatus != null)
            sharedStatus.notifySourceConsumerIsDone();
    }

    @Override
    public Long call() {
        return start();
    }

}
