package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.util.concurrent.Callable;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileParallelSource extends AbstractComponentSource implements Callable<Long>, FileComponent {

    private final SharedStatus sharedStatus;

    public FileParallelSource(FileReader source, SharedQueue target, SharedStatus sharedStatus, KOptions options) {
        super(source, target, options);
        this.sharedStatus = sharedStatus;
    }

    @Override
    protected void onFinally() {
        if(sharedStatus != null)
            sharedStatus.notifySourceConsumerIsDone();
    }

    @Override
    public Long call() throws Exception {
        return start();
    }
}
