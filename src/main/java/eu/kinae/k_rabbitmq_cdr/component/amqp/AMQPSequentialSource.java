package eu.kinae.k_rabbitmq_cdr.component.amqp;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AMQPSequentialSource extends AbstractComponentSource {

    public AMQPSequentialSource(AMQPConnection source, SharedQueue target, KOptions options) {
        super(source, target, options);
    }

}
