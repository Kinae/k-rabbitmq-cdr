package eu.kinae.k_rabbitmq_cdr.component.file;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentParallelSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileParallelSource extends AbstractComponentParallelSource {

    public FileParallelSource(FileReader source, SharedQueue target, KOptions options, SharedStatus sharedStatus) {
        super(source, target, options, sharedStatus);
    }

}
