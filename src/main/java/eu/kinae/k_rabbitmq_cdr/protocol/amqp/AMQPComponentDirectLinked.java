package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponent;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public class AMQPComponentDirectLinked extends AbstractComponent implements AMQPComponent {

    private final KOptions options;

    public AMQPComponentDirectLinked(AMQPConnection source, AMQPConnection target) {
        this(source, target, KOptions.DEFAULT);
    }

    public AMQPComponentDirectLinked(AMQPConnection source, AMQPConnection target, KOptions options) {
        super(source, target);
        this.options = options;
    }

    @Override
    protected void onFinally() {
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

}
