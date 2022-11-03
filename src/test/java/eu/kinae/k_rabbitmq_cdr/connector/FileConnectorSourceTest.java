package eu.kinae.k_rabbitmq_cdr.connector;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReaderInfo;
import eu.kinae.k_rabbitmq_cdr.connector.impl.FileConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class FileConnectorSourceTest {

    @TempDir
    protected Path tempDir;

    private final KParameters parameters = new KParameters(null, null, null, null, null, null, null, null, null, null, null, null, null);

    @Test
    public void Check_constructor() {
        var awsS3ReaderInfo = new FileReaderInfo(tempDir, KOptions.DEFAULT);
        var connector = new FileConnectorSource(awsS3ReaderInfo);
        assertThat(connector.getSupportedType()).isEqualTo(SupportedType.FILE);
        assertThat(connector.countMessages(parameters)).isEqualTo(0);
        assertThatNoException().isThrownBy(connector::close);
    }

    @Test
    public void Check_instance_of_direct_linked() {
        var options = KOptions.DEFAULT;
        var awsS3ReaderInfo = new FileReaderInfo(tempDir, KOptions.DEFAULT);
        var connector = new FileConnectorSource(awsS3ReaderInfo);
        var source = connector.getDirectLinked(parameters, options, null);
        assertThat(source).isInstanceOf(FileReader.class);
    }

    @Test
    public void Check_instance_of_sequential() {
        var options = KOptions.DEFAULT;
        var awsS3ReaderInfo = new FileReaderInfo(tempDir, KOptions.DEFAULT);
        var connector = new FileConnectorSource(awsS3ReaderInfo);
        var source = connector.getSequentialComponent(null, parameters, options, null);
        assertThat(source).isInstanceOf(SequentialComponentSource.class);
        assertThat(source).matches(it -> it.getSource() instanceof FileReader);
    }

    @Test
    public void Check_instance_of_parallels() {
        var options = KOptions.DEFAULT;
        var awsS3ReaderInfo = new FileReaderInfo(tempDir, KOptions.DEFAULT);
        var connector = new FileConnectorSource(awsS3ReaderInfo);
        var source = connector.getParallelComponents(null, parameters, options, null);
        assertThat(source).isInstanceOf(ParallelComponents.class);
        assertThat(source).hasOnlyElementsOfType(ParallelComponentSource.class).hasSize(options.sourceThread());
        assertThat(source).allMatch(it -> ((ParallelComponentSource) it).getSource() instanceof FileReader);
    }

}
