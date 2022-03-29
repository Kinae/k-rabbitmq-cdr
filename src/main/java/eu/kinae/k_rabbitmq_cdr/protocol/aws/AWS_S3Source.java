package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.protocol.Source;

public class AWS_S3Source extends AWS_S3Component implements Source {

    public AWS_S3Source() {
    }

    @Override
    public boolean run() {
        return false;
    }

}
