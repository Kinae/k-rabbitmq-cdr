package eu.kinae.k_rabbitmq_cdr.connector.impl;

import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.protocol.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPConnection;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSequentialSource;
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
    public Source getDirectLinked(KParameters parameters, KOptions options) {
        return new AMQPConnection(parameters.sourceURI(), parameters.sourceQueue());
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options) {
        AMQPConnection sConnection = new AMQPConnection(parameters.sourceURI(), parameters.sourceQueue());
        return new AMQPSequentialSource(sConnection, sharedQueue, options);
    }

    @Override
    public ParallelComponent getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnection connection = new AMQPConnection(parameters.sourceURI(), parameters.sourceQueue());
        return new AMQPParallelSource(connection, sharedQueue, options, sharedStatus);
    }
}
