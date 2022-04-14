package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public class AWS_S3Source extends AWS_S3Component implements Source {

    public AWS_S3Source() {
    }

    @Override public KMessage pop() throws Exception {
        return null;
    }

    //    @Override
    //    public boolean consume() {
    //        return false;
    //    }
    //
}
