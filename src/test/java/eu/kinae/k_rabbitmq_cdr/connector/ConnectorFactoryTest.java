package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConnectorFactoryTest {

    @Test
    public void Check_known_connectors() throws Exception {
        for(var entry : ConnectorFactory.knownConnectors.entrySet()) {
            Assertions.assertThat(ConnectorFactory.newConnector(entry.getKey().getKey(), entry.getKey().getValue())).isNotEmpty();
        }
    }

    @Test
    public void Check_impossible_connectors() throws Exception {
        Assertions.assertThat(ConnectorFactory.newConnector(SupportedType.AWS_S3, SupportedType.AWS_S3)).isEmpty();
        Assertions.assertThat(ConnectorFactory.newConnector(SupportedType.AWS_S3, SupportedType.FILE)).isEmpty();
        Assertions.assertThat(ConnectorFactory.newConnector(SupportedType.FILE, SupportedType.AWS_S3)).isEmpty();
        Assertions.assertThat(ConnectorFactory.newConnector(SupportedType.FILE, SupportedType.FILE)).isEmpty();
    }

}
