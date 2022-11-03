package eu.kinae.k_rabbitmq_cdr.connector;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.file.FileWriter;
import eu.kinae.k_rabbitmq_cdr.connector.impl.FileConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class FileConnectorTargetTest {

    @TempDir
    protected Path tempDir;

    private final KParameters parameters = new KParameters(null, null, null, null, null, null, null, null, null, null, null, null, null);

    @Test
    public void Check_constructor() {
        var connector = new FileConnectorTarget(tempDir);
        assertThat(connector.getSupportedType()).isEqualTo(SupportedType.FILE);
        assertThatNoException().isThrownBy(connector::close);
    }

    @Test
    public void Check_instance_of_direct_linked() {
        var connector = new FileConnectorTarget(tempDir);
        var Target = connector.getDirectLinked(parameters, null);
        assertThat(Target).isInstanceOf(FileWriter.class);
    }

    @Test
    public void Check_instance_of_sequential() {
        var options = KOptions.DEFAULT;
        var connector = new FileConnectorTarget(tempDir);
        var target = connector.getSequentialComponent(null, parameters, options, null);
        assertThat(target).isInstanceOf(SequentialComponentTarget.class);
        assertThat(target).matches(it -> it.getTarget() instanceof FileWriter);
    }

    @Test
    public void Check_instance_of_parallels() {
        var options = KOptions.DEFAULT;
        var connector = new FileConnectorTarget(tempDir);
        var target = connector.getParallelComponents(null, parameters, options, null);
        assertThat(target).isInstanceOf(ParallelComponents.class);
        assertThat(target).hasOnlyElementsOfType(ParallelComponentTarget.class).hasSize(options.targetThread());
        assertThat(target).allMatch(it -> ((ParallelComponentTarget) it).getTarget() instanceof FileWriter);
    }

}
