package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AWS_S3SequentialSource extends AbstractComponentSource {

    public AWS_S3SequentialSource(AWS_S3Reader source, SharedQueue target, KOptions options) {
        super(source, target, options);
    }

}
