package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public interface ConnectorTarget extends ConnectorType, AutoCloseable {

    Target getDirectLinked(KParameters parameters, SharedStatus sharedStatus);

    AbstractComponentTarget getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus);

    ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus);

}
