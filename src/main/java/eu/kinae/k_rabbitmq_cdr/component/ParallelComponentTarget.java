package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class ParallelComponentTarget extends AbstractComponentTarget implements ParallelComponent {

    private final SharedStatus sharedStatus;

    public ParallelComponentTarget(SharedQueue source, Target target, KOptions options, SharedStatus sharedStatus) {
        super(source, target, options);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return !sharedStatus.isConsumerAlive();
    }

    @Override
    public Long call() throws Exception {
        return start();
    }
}
