package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

abstract class AMQPComponentTarget extends AMQPComponent implements Target {

    protected final SharedBuffer sharedBuffer;
    protected final SharedStatus sharedStatus;

    protected AMQPComponentTarget(String uri, String queue, SharedBuffer sharedBuffer) throws Exception {
        this(uri, queue, sharedBuffer, null);
    }

    protected AMQPComponentTarget(String uri, String queue, SharedBuffer sharedBuffer, SharedStatus sharedStatus) throws Exception {
        super(uri, queue);
        this.sharedBuffer = sharedBuffer;
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected void onFinally() {

    }

    @Override
    protected long consumeNProduce() throws Exception {
        long count = 0;
        do {
            GetResponse response = sharedBuffer.pop();
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
