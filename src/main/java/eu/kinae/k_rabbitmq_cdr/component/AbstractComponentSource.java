package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public abstract class AbstractComponentSource extends AbstractComponent {

    protected final KOptions options;

    protected AbstractComponentSource(Source source, SharedQueue target, KOptions options) {
        super(source, target);
        this.options = options;
    }

    @Override
    protected void onFinally() {
    }

    @Override
    public long consumeNProduce() throws Exception {
        long count = 0;
        do {
            KMessage message = pop(options);
            if(message == null) {
                break;
            } else {
                push(message);
            }
        } while(++count < options.maxMessage() || options.maxMessage() == 0);
        return count;
    }

}
