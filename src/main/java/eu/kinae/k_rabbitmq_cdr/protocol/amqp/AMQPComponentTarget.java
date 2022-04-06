package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

abstract class AMQPComponentTarget extends AMQPComponent implements Source {

    protected final String queue;
    protected final SharedBuffer sharedBuffer;
    protected final SharedStatus sharedStatus;

    protected AMQPComponentTarget(SharedBuffer sharedBuffer, String uri, String queue) throws Exception {
        this(sharedBuffer, null, uri, queue);
    }

    protected AMQPComponentTarget(SharedBuffer sharedBuffer, SharedStatus sharedStatus, String uri, String queue) throws Exception {
        super(uri);
        this.sharedBuffer = sharedBuffer;
        this.sharedStatus = sharedStatus;
        this.queue = queue;
    }

}
