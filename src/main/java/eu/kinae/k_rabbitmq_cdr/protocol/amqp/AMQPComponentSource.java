package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import eu.kinae.k_rabbitmq_cdr.utils.SourceParams;

public class AMQPComponentSource extends AMQPComponent implements Source {

    protected final SharedBuffer sharedBuffer;
    protected final SharedStatus sharedStatus;
    protected final SourceParams parameters;

    protected AMQPComponentSource(SharedBuffer sharedBuffer, SourceParams parameters, String uri, String queue) throws Exception {
        this(sharedBuffer, null, parameters, uri, queue);
    }

    protected AMQPComponentSource(SharedBuffer sharedBuffer, SharedStatus sharedStatus, SourceParams parameters, String uri, String queue) throws Exception {
        super(uri, queue);
        this.sharedBuffer = sharedBuffer;
        this.sharedStatus = sharedStatus;
        this.parameters = parameters;
    }

    @Override
    public void start() {
        try {
            long start = System.currentTimeMillis();
            long count = consumeFromSource();
            long end = System.currentTimeMillis();
            logger.info("messages retrieved : {} in {}ms", count, (end - start));
        } catch(Exception e) {
            logger.error("Error : ", e);
        } finally {
            logger.info("SOURCE DONE");
            sharedStatus.notifySourceConsumerIsDone();
            close();
        }
    }

    private long consumeFromSource() throws Exception {
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
        } while(++count < parameters.getMaxMessage() || parameters.getMaxMessage() == 0); // add maximum message from params
        return count;
    }

}
