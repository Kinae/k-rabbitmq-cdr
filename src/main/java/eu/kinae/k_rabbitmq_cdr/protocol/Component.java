package eu.kinae.k_rabbitmq_cdr.protocol;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;

public interface Component extends AutoCloseable {

    SupportedType getSupportedType();

}
