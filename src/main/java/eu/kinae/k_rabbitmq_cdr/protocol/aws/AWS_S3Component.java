package eu.kinae.k_rabbitmq_cdr.protocol.aws;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;

public interface AWS_S3Component extends Component {

    @Override
    default SupportedType getSupportedType() {
        return SupportedType.AWS_S3;
    }
}
