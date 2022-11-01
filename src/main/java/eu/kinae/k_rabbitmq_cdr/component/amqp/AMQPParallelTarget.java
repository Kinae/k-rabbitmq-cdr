package eu.kinae.k_rabbitmq_cdr.component.amqp;

import com.rabbitmq.client.Channel;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPParallelTarget extends ParallelComponentTarget {

    private Channel channel;

    public AMQPParallelTarget(SharedQueue source, AMQPConnectionWriter target, KOptions options, SharedStatus sharedStatus) {
        super(source, target, options, sharedStatus);
    }

    // Use the same connection but create one channel per thread for maximum performance as recommended by RabbitMQ
    @Override
    public Long call() throws Exception {
        try(var channel = ((AMQPConnectionWriter) target).createChannel()) {
            this.channel = channel;
            return start();
        }
    }

    @Override
    public void push(KMessage message, KOptions options) throws Exception {
        if(target instanceof AMQPConnectionWriter connection) {
            connection.push(message, channel, options);
        } else {
            super.push(message, options);
        }
    }

}
