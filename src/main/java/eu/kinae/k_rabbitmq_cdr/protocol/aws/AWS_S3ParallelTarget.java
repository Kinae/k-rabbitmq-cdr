package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AWS_S3ParallelTarget extends AbstractComponentTarget implements Callable<Long>, AWS_S3Component {

    private final SharedStatus sharedStatus;

    public AWS_S3ParallelTarget(SharedQueue source, AWS_S3Writer target, SharedStatus sharedStatus) {
        super(source, target);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return !sharedStatus.isConsumerAlive();
    }

    @Override
    public Long call() {
        return start();
    }
}
