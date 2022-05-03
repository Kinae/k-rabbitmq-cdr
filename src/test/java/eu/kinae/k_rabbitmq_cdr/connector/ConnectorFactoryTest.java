package eu.kinae.k_rabbitmq_cdr.connector;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConnectorFactoryTest {

    @Test
    public void Check_source_connectors() throws Exception {
        for(var entrySource : ConnectorFactory.connectorSources.entrySet()) {
            for(var entryTarget : ConnectorFactory.connectorTargets.entrySet()) {
                Assertions.assertThat(ConnectorFactory.newConnector(entrySource.getKey(), entryTarget.getKey())).isNotEmpty();
            }
        }
    }

}
