package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends AMQPComponentTarget {

    public AMQPParallelTarget(JCommanderParams params, SharedBuffer sharedBuffer, SharedStatus sharedStatus) throws Exception {
        super(sharedBuffer, sharedStatus, params.targetURI, params.targetQueue);
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            long count = publish();
            long end = System.currentTimeMillis();
            logger.info("messages published : {} in {}ms", count, (end - start));
        } catch(Exception e) {
            logger.error("Error : ", e);
        } finally {
            logger.info("TARGET DONE");
            close();
        }
    }

    private long publish() throws Exception {
        long count = 0;
        GetResponse response;
        do {
            response = sharedBuffer.pop();
            if(response == null) {
                logger.info("Waiting for message ...");
                if(!sharedStatus.isConsumerAlive())
                    break;
            } else {
                count++;
                channel.basicPublish("", queue, false, false, response.getProps(), response.getBody());
            }
        } while(true);
        return count;
    }
}
