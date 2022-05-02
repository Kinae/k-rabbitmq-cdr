package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AWS_S3SequentialTarget extends AbstractComponentTarget implements AWS_S3Component {

    public AWS_S3SequentialTarget(SharedQueue source, AWS_S3Writer target) {
        super(source, target);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }

}
