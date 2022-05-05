package eu.kinae.k_rabbitmq_cdr.params;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KOptionsTest {

    @Test
    public void Check_constructor() {
        var jParams = new JCommanderParams();
        jParams.maxMessage = 33;
        jParams.threads = 6;
        jParams.sorted = true;

        var parameters = KOptions.of(jParams);
        assertThat(parameters).usingRecursiveComparison().isEqualTo(jParams);
    }

}
