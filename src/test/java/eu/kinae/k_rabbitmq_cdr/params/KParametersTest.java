package eu.kinae.k_rabbitmq_cdr.params;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import static org.assertj.core.api.Assertions.assertThat;

public class KParametersTest {

    @Test
    public void Check_constructor() {
        var jParams = new JCommanderParams();
        jParams.sourceType = SupportedType.AMQP;
        jParams.sourceURI = "sourceURI";
        jParams.sourceQueue = "sourceQueue";
        jParams.targetType = SupportedType.FILE;
        jParams.targetURI = "targetURI";
        jParams.targetQueue = "targetQueue";
        jParams.directory = "directory";
        jParams.region = Region.AWS_GLOBAL;
        jParams.bucket = "bucket";
        jParams.prefix = "prefix";
        jParams.transferType = TransferType.DIRECT;
        jParams.processType = ProcessType.SEQUENTIAL;

        var parameters = KParameters.of(jParams);
        assertThat(parameters).usingRecursiveComparison().isEqualTo(jParams);
    }

}
