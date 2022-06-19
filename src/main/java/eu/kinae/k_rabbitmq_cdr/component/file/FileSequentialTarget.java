package eu.kinae.k_rabbitmq_cdr.component.file;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class FileSequentialTarget extends AbstractComponentTarget {

    public FileSequentialTarget(SharedQueue source, FileWriter target, KOptions options) {
        super(source, target, options);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }

}
