package eu.kinae.k_rabbitmq_cdr.component.aws;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentParallelSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AWS_S3ParallelSource extends AbstractComponentParallelSource {

    public AWS_S3ParallelSource(AWS_S3Reader source, SharedQueue target, KOptions options, SharedStatus sharedStatus) {
        super(source, target, options, sharedStatus);
    }

}
