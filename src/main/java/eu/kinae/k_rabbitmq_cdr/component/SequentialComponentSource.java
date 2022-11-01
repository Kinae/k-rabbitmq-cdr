package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public class SequentialComponentSource extends AbstractComponentSource {

    public SequentialComponentSource(Source source, SharedQueue target, KOptions options) {
        super(source, target, options);
    }

}
