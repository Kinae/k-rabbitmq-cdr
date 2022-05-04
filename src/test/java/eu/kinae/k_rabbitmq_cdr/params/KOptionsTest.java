package eu.kinae.k_rabbitmq_cdr.params;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class KOptionsTest {

    @Test
    public void Check_constructor() {
        var jParams = new JCommanderParams();
        jParams.maxMessage = 33;
        jParams.threads = 6;
        jParams.sorted = true;

        var parameters = KOptions.of(jParams);
        Assertions.assertThat(parameters).usingRecursiveComparison().isEqualTo(jParams);
    }

}
