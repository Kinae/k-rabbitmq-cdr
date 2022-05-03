package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.protocol.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileSequentialSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileConnectorSource implements ConnectorSource {

    public FileConnectorSource() {
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options) {
        return new FileReader(Path.of(parameters.directory()), options);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options) {
        FileReader reader = new FileReader(Path.of(parameters.directory()), options);
        return new FileSequentialSource(reader, sharedQueue, options);
    }

    @Override
    public ParallelComponent getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        FileReader reader = new FileReader(Path.of(parameters.directory()), options);
        return new FileParallelSource(reader, sharedQueue, options, sharedStatus);
    }
}
