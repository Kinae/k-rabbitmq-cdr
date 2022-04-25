package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileParallelTarget extends FileComponentTarget implements Callable<Long> {

    private final SharedStatus sharedStatus;

    public FileParallelTarget(SharedQueue source, FileWriterTarget target, SharedStatus sharedStatus) {
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
