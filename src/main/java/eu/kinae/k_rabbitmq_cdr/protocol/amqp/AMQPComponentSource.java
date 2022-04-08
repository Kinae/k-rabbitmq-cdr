package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPComponentSource extends AMQPComponent implements Source {

    protected final SharedBuffer sharedBuffer;
    protected final SharedStatus sharedStatus;
    protected final KOptions options;

    protected AMQPComponentSource(String uri, String queue, SharedBuffer sharedBuffer, KOptions options) throws Exception {
        this(uri, queue, sharedBuffer, null, options);
    }

    protected AMQPComponentSource(String uri, String queue, SharedBuffer sharedBuffer, SharedStatus sharedStatus, KOptions options) throws Exception {
        super(uri, queue);
        this.sharedBuffer = sharedBuffer;
        this.sharedStatus = sharedStatus;
        this.options = options;
    }

    @Override
    protected void onFinally() {
        sharedStatus.notifySourceConsumerIsDone();
    }

    @Override
    protected long consumeNProduce() throws Exception {
        long count = 0;
        do {
            GetResponse response = basicGet();
            if(response == null) {
                logger.debug("no more message to get");
                break;
            } else {
                if(count == 0)
                    logger.info("estimate total number of messages : {}", (response.getMessageCount() + 1));
                sharedBuffer.push(response);
            }
        } while(++count < options.getMaxMessage() || options.getMaxMessage() == 0); // add maximum message from params
        return count;
    }

}
