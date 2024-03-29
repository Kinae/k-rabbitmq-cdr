package eu.kinae.k_rabbitmq_cdr.component.aws;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponent;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class AWS_S3SequentialSourceTest extends AWS_S3AbstractComponentSourceTest {

    @Override
    protected AbstractComponent getComponent(Source source, Target target, KOptions options) {
        return new SequentialComponentSource(source, (SharedQueue) target, options);
    }

    @Override
    protected SharedQueue getSharedQueue() {
        return new SharedQueue(ProcessType.SEQUENTIAL);
    }

}
