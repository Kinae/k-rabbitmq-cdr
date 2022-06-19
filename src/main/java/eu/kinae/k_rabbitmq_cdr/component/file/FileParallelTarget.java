package eu.kinae.k_rabbitmq_cdr.component.file;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentParallelTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileParallelTarget extends AbstractComponentParallelTarget {

    public FileParallelTarget(SharedQueue source, FileWriter target, KOptions options, SharedStatus sharedStatus) {
        super(source, target, options, sharedStatus);
    }

}
