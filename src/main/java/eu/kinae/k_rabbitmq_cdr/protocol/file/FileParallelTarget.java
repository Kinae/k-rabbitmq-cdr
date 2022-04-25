package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileParallelTarget extends AbstractComponentTarget implements Callable<Long>, FileComponent {

    private final SharedStatus sharedStatus;

    public FileParallelTarget(SharedQueue source, FileWriter target, SharedStatus sharedStatus) {
        super(source, target);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected boolean stopConsumingIfResponseIsNull() {
        return !sharedStatus.isConsumerAlive();
    }

    @Override
    public Long call() {
        return start();
    }
}
