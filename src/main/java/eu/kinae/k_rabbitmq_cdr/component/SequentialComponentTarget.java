package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class SequentialComponentTarget extends AbstractComponentTarget {

    public SequentialComponentTarget(SharedQueue source, Target target, KOptions options) {
        super(source, target, options);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }

}
