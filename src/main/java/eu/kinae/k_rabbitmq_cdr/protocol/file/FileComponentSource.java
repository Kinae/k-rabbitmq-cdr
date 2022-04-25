package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

abstract class FileComponentSource extends FileComponent {

    protected final SharedStatus sharedStatus;
    protected final KOptions options;

    protected FileComponentSource(FileReaderSource source, SharedQueue target, KOptions options) {
        this(source, target, null, options);
    }

    protected FileComponentSource(FileReaderSource source, SharedQueue target, SharedStatus sharedStatus, KOptions options) {
        super(source, target);
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
