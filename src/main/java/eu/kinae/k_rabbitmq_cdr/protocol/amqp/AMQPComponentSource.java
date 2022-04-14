package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

abstract class AMQPComponentSource extends AMQPComponent {

    protected final SharedStatus sharedStatus;
    protected final KOptions options;

    protected AMQPComponentSource(AMQPConnection connection, SharedQueue sharedQueue, KOptions options) {
        this(connection, sharedQueue, null, options);
    }

    protected AMQPComponentSource(AMQPConnection connection, SharedQueue sharedQueue, SharedStatus sharedStatus, KOptions options) {
        super(connection, sharedQueue);
        this.sharedStatus = sharedStatus;
        this.options = options;
    }

    @Override
    protected void onFinally() {
        sharedStatus.notifySourceConsumerIsDone();
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
                if(count++ == 0)
                    logger.info("estimate total number of messages : {}", (message.messageCount() + 1));
                push(message);
            }
        } while(count < options.maxMessage() || options.maxMessage() == 0); // add maximum message from params
        return count;
    }
}
