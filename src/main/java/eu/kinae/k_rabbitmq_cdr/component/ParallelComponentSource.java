package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class ParallelComponentSource extends AbstractComponentSource implements ParallelComponent {

    private final SharedStatus sharedStatus;

    public ParallelComponentSource(Source source, SharedQueue target, KOptions options, SharedStatus sharedStatus) {
        super(source, target, options);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected void onFinally() {
        if(sharedStatus != null) {
            sharedStatus.notifySourceConsumerIsDone();
        }
    }

    @Override
    public Long call() {
        return start();
    }
}
