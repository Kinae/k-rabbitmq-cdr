package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.SharedBuffer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import eu.kinae.k_rabbitmq_cdr.utils.SourceParams;

abstract class AMQPComponentSource extends AMQPComponent implements Source {

    protected final String queue;
    protected final SharedBuffer sharedBuffer;
    protected final SharedStatus sharedStatus;
    protected final SourceParams parameters;

    protected AMQPComponentSource(SharedBuffer sharedBuffer, String uri, String queue) throws Exception {
        this(sharedBuffer, null, null, uri, queue);
    }

    protected AMQPComponentSource(SharedBuffer sharedBuffer, SourceParams parameters, String uri, String queue) throws Exception {
        this(sharedBuffer, null, parameters, uri, queue);
    }

    protected AMQPComponentSource(SharedBuffer sharedBuffer, SharedStatus sharedStatus, SourceParams parameters, String uri, String queue) throws Exception {
        super(uri);
        this.sharedBuffer = sharedBuffer;
        this.sharedStatus = sharedStatus;
        this.parameters = parameters;
        this.queue = queue;
    }

}
