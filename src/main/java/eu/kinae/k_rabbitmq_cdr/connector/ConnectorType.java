package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;

public interface ConnectorType {

    SupportedType getSupportedType();
}
