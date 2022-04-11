package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

abstract class AMQPComponentTarget extends AMQPComponent implements Target {

    protected final SharedQueue sharedQueue;
    protected final SharedStatus sharedStatus;

    protected AMQPComponentTarget(String uri, String queue, SharedQueue sharedQueue) throws Exception {
        this(uri, queue, sharedQueue, null);
    }

    protected AMQPComponentTarget(String uri, String queue, SharedQueue sharedQueue, SharedStatus sharedStatus) throws Exception {
        super(uri, queue);
        this.sharedQueue = sharedQueue;
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected void onFinally() {

    }

    @Override
    protected long consumeNProduce() throws Exception {
        long count = 0;
        do {
            GetResponse response = sharedQueue.pop();
            if(response == null) {
                logger.debug("Waiting for message ...");
                if(breakIfResponseIsNull())
                    break;
            } else {
                count++;
                basicPublish(response);
            }
        } while(true);
        return count;
    }

    protected abstract boolean breakIfResponseIsNull();

}
