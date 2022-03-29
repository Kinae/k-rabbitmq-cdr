package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;

public abstract class AMQPComponent implements Component {

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }
}
