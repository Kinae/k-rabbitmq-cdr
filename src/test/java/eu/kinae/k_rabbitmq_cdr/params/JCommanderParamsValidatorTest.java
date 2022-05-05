package eu.kinae.k_rabbitmq_cdr.params;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JCommanderParamsValidatorTest {

    @Test
    public void Check_no_exception_is_thrown() {
        var jParams = new JCommanderParams();
        jParams.sourceType = SupportedType.AMQP;
        jParams.sourceURI = "sourceURI";
        jParams.sourceQueue = "sourceQueue";
        jParams.targetType = SupportedType.FILE;
        jParams.directory = "directory";

        JCommanderParamsValidator.validate(jParams);
    }

    @Test
    public void Check_missing_AMQP_parameter() {
        var jParams = new JCommanderParams();
        jParams.sourceType = SupportedType.AMQP;
        jParams.targetType = SupportedType.AMQP;

        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
        jParams.sourceURI = "sourceURI";
        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
        jParams.sourceQueue = "sourceQueue";
        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
        jParams.targetURI = "targetURI";
        jParams.targetQueue = "targetQueue";
        JCommanderParamsValidator.validate(jParams);
    }

    @Test
    public void Check_missing_FILE_parameter() {
        var jParams = new JCommanderParams();
        jParams.sourceType = SupportedType.FILE;
        jParams.targetType = SupportedType.AMQP;
        jParams.targetURI = "targetURI";
        jParams.targetQueue = "targetQueue";

        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
        jParams.directory = "directory";
        JCommanderParamsValidator.validate(jParams);
    }

    @Test
    public void Check_missing_AWS_S3_parameter() {
        var jParams = new JCommanderParams();
        jParams.sourceType = SupportedType.AWS_S3;
        jParams.targetType = SupportedType.AMQP;
        jParams.targetURI = "targetURI";
        jParams.targetQueue = "targetQueue";

        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
        jParams.region = Region.AWS_GLOBAL;
        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
        jParams.bucket = "bucket";
        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
        jParams.prefix = "prefix";
        JCommanderParamsValidator.validate(jParams);
    }

    @Test
    public void Check_impossible_connectors() {
        var jParams = new JCommanderParams();
        checkImpossibleConnector(jParams, SupportedType.FILE, SupportedType.FILE);
        checkImpossibleConnector(jParams, SupportedType.FILE, SupportedType.AWS_S3);
        checkImpossibleConnector(jParams, SupportedType.AWS_S3, SupportedType.AWS_S3);
        checkImpossibleConnector(jParams, SupportedType.AWS_S3, SupportedType.FILE);
    }

    private void checkImpossibleConnector(JCommanderParams jParams, SupportedType file, SupportedType file2) {
        jParams.sourceType = file;
        jParams.targetType = file2;
        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
    }

}
