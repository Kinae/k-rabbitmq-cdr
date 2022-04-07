package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

abstract class AMQPComponentTarget extends AMQPComponent implements Target {

    protected final SharedBuffer sharedBuffer;
    protected final SharedStatus sharedStatus;

    protected AMQPComponentTarget(SharedBuffer sharedBuffer, String uri, String queue) throws Exception {
        this(sharedBuffer, null, uri, queue);
    }

    protected AMQPComponentTarget(SharedBuffer sharedBuffer, SharedStatus sharedStatus, String uri, String queue) throws Exception {
        super(uri, queue);
        this.sharedBuffer = sharedBuffer;
        this.sharedStatus = sharedStatus;
    }

    public void start() {
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
                logger.info("Waiting for message ...");
                if(!sharedStatus.isConsumerAlive())
                    break;
            } else {
                count++;
                basicPublish(response);
            }
        } while(true);
        return count;
    }

}
