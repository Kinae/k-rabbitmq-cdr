package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import eu.kinae.k_rabbitmq_cdr.utils.SourceParams;

public class AMQPParallelSource extends AMQPComponentSource implements Runnable {

    public AMQPParallelSource(JCommanderParams params, SharedBuffer sharedBuffer, SharedStatus sharedStatus, SourceParams parameters) throws Exception {
        super(sharedBuffer, sharedStatus, parameters, params.sourceURI, params.sourceQueue);
    }

    @Override
    public void run() {
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
            GetResponse response = channel.basicGet(queue, false);
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
