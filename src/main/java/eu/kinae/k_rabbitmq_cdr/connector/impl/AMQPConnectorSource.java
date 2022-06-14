package eu.kinae.k_rabbitmq_cdr.connector.impl;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnectionReader;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPParallelSource;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPSequentialSource;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPConnectorSource implements ConnectorSource {

    public AMQPConnectorSource() {
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return new AMQPConnectionReader(parameters.sourceURI(), parameters.sourceQueue(), sharedStatus);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnectionReader source = new AMQPConnectionReader(parameters.sourceURI(), parameters.sourceQueue(), sharedStatus);
        return new AMQPSequentialSource(source, sharedQueue, options);
    }

    @Override
    public ParallelComponent getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnectionReader source = new AMQPConnectionReader(parameters.sourceURI(), parameters.sourceQueue(), sharedStatus);
        return new AMQPParallelSource(source, sharedQueue, options, sharedStatus);
    }
}
