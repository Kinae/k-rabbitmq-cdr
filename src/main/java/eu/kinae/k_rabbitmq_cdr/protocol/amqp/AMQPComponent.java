package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import eu.kinae.k_rabbitmq_cdr.protocol.Engine;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public abstract class AMQPComponent extends Engine implements AutoCloseable, Component, Source, Target {

    protected final Source source;
    protected final Target target;

    protected AMQPComponent(Source source, Target target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

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

    public Source getSource() {
        return source;
    }

    public Target getTarget() {
        return target;
    }
}
