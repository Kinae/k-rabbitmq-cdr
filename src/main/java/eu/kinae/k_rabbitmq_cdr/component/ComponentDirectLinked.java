package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public class ComponentDirectLinked extends AbstractComponent {

    private final KOptions options;

    public ComponentDirectLinked(Source source, Target target, KOptions options) {
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
