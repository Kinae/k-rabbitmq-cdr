package eu.kinae.k_rabbitmq_cdr.component.file;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSourceTest;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import static org.mockito.Mockito.mock;

public abstract class FileAbstractComponentSourceTest extends AbstractComponentSourceTest {

    @TempDir
    protected Path tempDir;

    @TempDir
    protected Path tempEmptyDir;

    @BeforeEach
    public void beforeEach() throws Exception {
        var writer = new FileWriter(tempDir);
        for(var message : MESSAGES) {
            writer.push(message);
        }
    }

    @Override
    protected FileReader getEmptySource() {
        return new FileReader(new FileReaderInfo(tempEmptyDir, KOptions.DEFAULT));
    }

    @Override
    protected FileReader getSource(KOptions options) {
        return new FileReader(new FileReaderInfo(tempDir, options));
    }

    protected SharedQueue getMockTarget() {
        return mock(SharedQueue.class);
    }


}
