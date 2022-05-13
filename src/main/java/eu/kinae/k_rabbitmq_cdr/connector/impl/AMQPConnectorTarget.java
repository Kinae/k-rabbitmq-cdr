package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnection;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPParallelTarget;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPSequentialTarget;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPConnectorTarget implements ConnectorTarget {

    public AMQPConnectorTarget() {
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    @Override
    public Target getDirectLinked(KParameters parameters, SharedStatus sharedStatus) {
        return new AMQPConnection(parameters.targetURI(), parameters.targetQueue(), sharedStatus);
    }

    @Override
    public AbstractComponentTarget getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, SharedStatus sharedStatus) {
        AMQPConnection tConnection = new AMQPConnection(parameters.targetURI(), parameters.targetQueue(), sharedStatus);
        return new AMQPSequentialTarget(sharedQueue, tConnection);
    }

    @Override
    public ParallelComponents getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnection connection = new AMQPConnection(parameters.targetURI(), parameters.targetQueue(), sharedStatus);
        return IntStream.range(0, options.threads())
                .mapToObj(ignored -> new AMQPParallelTarget(sharedQueue, connection, sharedStatus))
                .collect(Collectors.toCollection(ParallelComponents::new));
    }
}
