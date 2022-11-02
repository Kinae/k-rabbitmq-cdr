package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public interface ConnectorSource extends ConnectorType, AutoCloseable {

    long countMessages(KParameters parameters);

    Source getDirectLinked(KParameters parameters, KOptions options, SharedStatus sharedStatus);

    AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus);

    ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus);

}
