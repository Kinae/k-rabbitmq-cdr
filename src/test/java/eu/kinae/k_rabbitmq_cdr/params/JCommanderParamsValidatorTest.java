package eu.kinae.k_rabbitmq_cdr.params;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
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

        assertThatNoException().isThrownBy(() -> JCommanderParamsValidator.validate(jParams));
    }

    @Test
    public void Check_threads_are_forced_to_one_for_sorted() {

        var jParams = new JCommanderParams();
        jParams.sourceType = SupportedType.AMQP;
        jParams.sourceURI = "sourceURI";
        jParams.sourceQueue = "sourceQueue";
        jParams.targetType = SupportedType.FILE;
        jParams.directory = "directory";
        jParams.sorted = true;
        jParams.transferType = TransferType.BUFFERED;
        jParams.processType = ProcessType.PARALLEL;
        jParams.sourceThread = 5;
        jParams.targetThread = 10;

        assertThatNoException().isThrownBy(() -> JCommanderParamsValidator.validate(jParams));
        assertThat(jParams.sourceThread).isEqualTo(1);
        assertThat(jParams.targetThread).isEqualTo(1);
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
        assertThatNoException().isThrownBy(() -> JCommanderParamsValidator.validate(jParams));
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
        assertThatNoException().isThrownBy(() -> JCommanderParamsValidator.validate(jParams));
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
        assertThatNoException().isThrownBy(() -> JCommanderParamsValidator.validate(jParams));
    }

    @Test
    public void Check_impossible_connectors() {
        var jParams = new JCommanderParams();
        checkImpossibleConnector(jParams, SupportedType.FILE, SupportedType.FILE);
        checkImpossibleConnector(jParams, SupportedType.FILE, SupportedType.AWS_S3);
        checkImpossibleConnector(jParams, SupportedType.AWS_S3, SupportedType.AWS_S3);
        checkImpossibleConnector(jParams, SupportedType.AWS_S3, SupportedType.FILE);
    }

    private void checkImpossibleConnector(JCommanderParams jParams, SupportedType sType, SupportedType tType) {
        jParams.sourceType = sType;
        jParams.targetType = tType;
        assertThatThrownBy(() -> JCommanderParamsValidator.validate(jParams)).isInstanceOf(IllegalArgumentException.class);
    }

}
