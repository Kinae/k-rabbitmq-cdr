package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public class AMQPComponentDirectLinked extends AMQPComponent {

    private final KOptions options;

    public AMQPComponentDirectLinked(AMQPConnection source, AMQPConnection target) {
        this(source, target, KOptions.DEFAULT);
    }

    public AMQPComponentDirectLinked(AMQPConnection source, AMQPConnection target, KOptions options) {
        super(source, target);
        this.options = options;
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    @Override
    public long consumeNProduce() throws Exception {
        long count = 0;
        do {
            KMessage message = pop();
            if(message == null) {
                logger.debug("no more message to get");
                break;
            } else {
                if(count == 0)
                    logger.info("estimate total number of messages : {}", (message.messageCount() + 1));
                push(message);
            }
        } while(++count < options.maxMessage() || options.maxMessage() == 0); // add message numbers (range, specific number)
        return count;
    }

    @Override
    protected void onFinally() {
    }
}
