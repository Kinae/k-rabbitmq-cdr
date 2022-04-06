package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;

public class AMQPSequentialTarget extends AMQPComponentTarget {

    public AMQPSequentialTarget(JCommanderParams params, SharedBuffer sharedBuffer) throws Exception {
        super(sharedBuffer, params.targetURI, params.targetQueue);
    }

    public void run() {
        try {
            long start = System.currentTimeMillis();
            long count = publishToTarget();
            long end = System.currentTimeMillis();
            logger.info("messages published : {} in {}ms", count, (end - start));
        } catch(Exception e) {
            logger.error("Error : ", e);
        } finally {
            close();
        }
    }

    private long publishToTarget() throws Exception {
        long count = 0;
        do {
            GetResponse response = sharedBuffer.pop();
            if(response == null) {
                logger.debug("No more message to get, shared buffer is empty");
                break;
            } else {
                count++;
                channel.basicPublish("", queue, false, false, response.getProps(), response.getBody());
            }
        } while(true);
        return count;
    }
}
