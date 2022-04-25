package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;

public interface AMQPComponent extends Component {

    @Override
    default SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }
}
