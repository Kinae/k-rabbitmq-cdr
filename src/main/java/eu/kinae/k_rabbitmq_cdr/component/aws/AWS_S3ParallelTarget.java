package eu.kinae.k_rabbitmq_cdr.component.aws;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentParallelTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AWS_S3ParallelTarget extends AbstractComponentParallelTarget {

    public AWS_S3ParallelTarget(SharedQueue source, AWS_S3Writer target, SharedStatus sharedStatus) {
        super(source, target, sharedStatus);
    }
}
