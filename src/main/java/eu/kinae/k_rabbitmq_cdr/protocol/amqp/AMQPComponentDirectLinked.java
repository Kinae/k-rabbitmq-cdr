package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import eu.kinae.k_rabbitmq_cdr.protocol.Engine;

public class AMQPComponentDirectLinked extends Engine implements Component {

    private final AMQPConnection source;
    private final AMQPConnection target;
    private final KOptions options;

    public AMQPComponentDirectLinked(JCommanderParams params, KOptions options) throws Exception {
        this.source = new AMQPConnection(params.sourceURI, params.sourceQueue);
        this.target = new AMQPConnection(params.targetURI, params.targetQueue);
        this.options = options;
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    @Override
    protected long consumeNProduce() throws Exception {
        long count = 0;
        do {
            GetResponse response = source.basicGet();
            if(response == null) {
                logger.debug("no more message to get");
                break;
            } else {
                if(count == 0)
                    logger.info("estimate total number of messages : {}", (response.getMessageCount() + 1));
                target.basicPublish(response);
            }
        } while(++count < options.maxMessage() || options.maxMessage() == 0); // add message numbers (range, specific number)
        return count;
    }

    @Override
    protected void onFinally() {
    }

    @Override
    protected void close() {
        source.close();
        target.close();
    }

}
