package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractComponent implements Component, Source, Target {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Source source;
    protected final Target target;

    protected AbstractComponent(Source source, Target target) {
        this.source = source;
        this.target = target;
    }

    public abstract long consumeNProduce() throws Exception;

    protected abstract void onFinally();

    @Override
    public KMessage pop() throws Exception {
        return source.pop();
    }

    @Override
    public void push(KMessage message) throws Exception {
        target.push(message);
    }

    @Override
    public void close() throws Exception {
        source.close();
        target.close();
    }

    public long start() {
        try {
            return consumeNProduce();
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        } finally {
            onFinally();
        }
    }

    public Source getSource() {
        return source;
    }

    public Target getTarget() {
        return target;
    }

}
