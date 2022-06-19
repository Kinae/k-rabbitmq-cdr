package eu.kinae.k_rabbitmq_cdr.component.aws;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AWS_S3SequentialTarget extends AbstractComponentTarget {

    public AWS_S3SequentialTarget(SharedQueue source, AWS_S3Writer target, KOptions options) {
        super(source, target, options);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }

}
