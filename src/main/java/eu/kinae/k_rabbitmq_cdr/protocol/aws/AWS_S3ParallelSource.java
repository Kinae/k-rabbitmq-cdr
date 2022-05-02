package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AWS_S3ParallelSource extends AbstractComponentSource implements Callable<Long>, AWS_S3Component {

    private final SharedStatus sharedStatus;

    public AWS_S3ParallelSource(AWS_S3Reader source, SharedQueue target, SharedStatus sharedStatus, KOptions options) {
        super(source, target, options);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected void onFinally() {
        if(sharedStatus != null)
            sharedStatus.notifySourceConsumerIsDone();
    }

    @Override
    public Long call() throws Exception {
        return start();
    }
}
