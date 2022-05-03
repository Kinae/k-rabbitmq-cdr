package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentParallelTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileParallelTarget extends AbstractComponentParallelTarget {

    public FileParallelTarget(SharedQueue source, FileWriter target, SharedStatus sharedStatus) {
        super(source, target, sharedStatus);
    }

}
