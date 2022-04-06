package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.utils.SourceParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPDirectTransfer {

    private final AMQPSequentialSource source;
    private final AMQPSequentialTarget target;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AMQPDirectTransfer(JCommanderParams params, SourceParams sourceParams) throws Exception {
        this.source = new AMQPSequentialSource(params, null, sourceParams);
        this.target = new AMQPSequentialTarget(params, null);
    }

    public void run() {
        try {
            long start = System.currentTimeMillis();
            long count = consumeNProduce();
            long end = System.currentTimeMillis();
            logger.info("messages retrieved : {} in {}ms", count, (end - start));
        } catch(Exception e) {
            logger.error("Error : ", e);
        } finally {
            logger.info("SOURCE DONE");
            source.close();
            target.close();
        }
    }

    private long consumeNProduce() throws Exception {
        long count = 0;
        do {
            GetResponse response = source.channel.basicGet(source.queue, false);
            if(response == null) {
                logger.debug("no more message to get");
                break;
            } else {
                if(count == 0)
                    logger.info("estimate total number of messages : {}", (response.getMessageCount() + 1));
                target.channel.basicPublish("", target.queue, false, false, response.getProps(), response.getBody());
            }
        } while(++count < source.parameters.getMaxMessage() || source.parameters.getMaxMessage() == 0); // add message numbers (range, specific number)
        return count;
    }

}
