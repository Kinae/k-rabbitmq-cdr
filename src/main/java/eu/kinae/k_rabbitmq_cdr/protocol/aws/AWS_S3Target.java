package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public class AWS_S3Target extends AWS_S3Component implements Target {

    public AWS_S3Target() {
    }

    @Override public void push(KMessage message) throws Exception {

    }

    @Override public void close() throws Exception {

    }
}
