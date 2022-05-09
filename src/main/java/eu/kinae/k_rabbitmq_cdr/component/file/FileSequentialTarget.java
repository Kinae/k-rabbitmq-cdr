package eu.kinae.k_rabbitmq_cdr.component.file;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class FileSequentialTarget extends AbstractComponentTarget {

    public FileSequentialTarget(SharedQueue source, FileWriter target) {
        super(source, target);
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return true;
    }

}
