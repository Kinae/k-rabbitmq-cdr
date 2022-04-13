package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;

public abstract class AWS_S3Component implements Component {

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AWS_S3;
    }

    @Override
    public void close() throws Exception {

    }
}
