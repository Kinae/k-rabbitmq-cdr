package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileParallelSource extends FileComponentSource implements Callable<Long> {

    public FileParallelSource(FileReaderSource source, SharedQueue target, SharedStatus sharedStatus, KOptions options) {
        super(source, target, sharedStatus, options);
    }

    @Override
    public Long call() throws Exception {
        return start();
    }
}
