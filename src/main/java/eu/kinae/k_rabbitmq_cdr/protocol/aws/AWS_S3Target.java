package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.protocol.Target;

public class AWS_S3Target extends AWS_S3Component implements Target {

    public AWS_S3Target() {
    }

    @Override
    public boolean run() {
        return false;
    }

}
