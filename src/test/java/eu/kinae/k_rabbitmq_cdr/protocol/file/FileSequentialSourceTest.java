package eu.kinae.k_rabbitmq_cdr.protocol.file;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class FileSequentialSourceTest extends FileAbstractComponentSourceTest {

    @Override
    protected AbstractComponent getComponent(Source source, Target target, KOptions options) {
        return new FileSequentialSource((FileReader) source, (SharedQueue) target, options);
    }

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.SEQUENTIAL);
    }

}
