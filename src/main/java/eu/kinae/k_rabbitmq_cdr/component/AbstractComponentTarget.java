package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;

public abstract class AbstractComponentTarget extends AbstractComponent {

    protected final KOptions options;

    protected AbstractComponentTarget(SharedQueue source, Target target, KOptions options) {
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
                if(stopConsumingIfResponseIsNull()) {
                    break;
                }
            } else {
                count++;
                push(message);
            }
        } while(true);
        return count;
    }

    protected abstract boolean stopConsumingIfResponseIsNull();

}
