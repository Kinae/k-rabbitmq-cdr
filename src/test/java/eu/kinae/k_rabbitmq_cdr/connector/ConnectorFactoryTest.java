package eu.kinae.k_rabbitmq_cdr.connector;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectorFactoryTest {

    @Test
    public void Check_to_instantiate_all_connectors() throws Exception {
        for(var entrySource : ConnectorFactory.connectorSources.entrySet()) {
            for(var entryTarget : ConnectorFactory.connectorTargets.entrySet()) {
                assertThat(ConnectorFactory.newConnector(entrySource.getKey(), entryTarget.getKey())).isNotEmpty();
            }
        }
    }



}
